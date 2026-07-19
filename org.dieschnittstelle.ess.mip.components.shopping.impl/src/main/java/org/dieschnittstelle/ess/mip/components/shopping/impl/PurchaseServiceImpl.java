package org.dieschnittstelle.ess.mip.components.shopping.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.Customer;
import org.dieschnittstelle.ess.entities.crm.CustomerTransaction;
import org.dieschnittstelle.ess.entities.crm.CustomerTransactionShoppingCartItem;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.ProductBundle;
import org.dieschnittstelle.ess.entities.shopping.ShoppingCartItem;
import org.dieschnittstelle.ess.mip.components.crm.api.CampaignTracking;
import org.dieschnittstelle.ess.mip.components.crm.api.CustomerTracking;
import org.dieschnittstelle.ess.mip.components.crm.crud.api.CustomerCRUD;
import org.dieschnittstelle.ess.mip.components.crm.crud.api.TouchpointCRUD;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.mip.components.shopping.api.PurchaseService;
import org.dieschnittstelle.ess.mip.components.shopping.api.ShoppingException;
import org.dieschnittstelle.ess.mip.components.shopping.cart.api.ShoppingCart;
import org.dieschnittstelle.ess.mip.components.shopping.cart.api.ShoppingCartService;
import org.dieschnittstelle.ess.mip.components.shopping.cart.impl.ShoppingCartEntity;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Logged
@RequestScoped
public class PurchaseServiceImpl implements PurchaseService {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(PurchaseServiceImpl.class);

    /*
     * the three beans that are used
     */
    private ShoppingCart shoppingCart;

    @Inject
    private CustomerTracking customerTracking;

    @Inject
    private CampaignTracking campaignTracking;

    @Inject
    private CustomerCRUD customerCRUD;

    @Inject
    private TouchpointCRUD touchpointAccess;

    @Inject
    private ProductCRUD productCRUD;

    @Inject
    private StockSystem stockSystem;

    /**
     * the customer
     */
    private Customer customer;

    /**
     * the touchpoint
     */
    private AbstractTouchpoint touchpoint;
    @Inject
    private ShoppingCartService shoppingCartService;

    /*
     * verify whether campaigns are still valid
     */
    public void verifyCampaigns() throws ShoppingException {
        if (this.customer == null || this.touchpoint == null) {
            throw new RuntimeException("cannot verify campaigns! No touchpoint has been set!");
        }

        for (ShoppingCartItem item : this.shoppingCart.getItems()) {
            if (item.isCampaign()) {
                int availableCampaigns = this.campaignTracking.existsValidCampaignExecutionAtTouchpoint(
                        item.getErpProductId(), this.touchpoint);
                logger.info("got available campaigns for product " + item.getErpProductId() + ": "
                        + availableCampaigns);
                // we check whether we have sufficient campaign items available
                if (availableCampaigns < item.getUnits()) {
                    throw new ShoppingException("verifyCampaigns() failed for productBundle " + item
                            + " at touchpoint " + this.touchpoint + "! Need " + item.getUnits()
                            + " instances of campaign, but only got: " + availableCampaigns);
                }
            }
        }
    }

    public void purchase()  throws ShoppingException {
        logger.info("purchase()");

        if (this.customer == null || this.touchpoint == null) {
            throw new RuntimeException(
                    "cannot commit shopping session! Either customer or touchpoint has not been set: " + this.customer
                            + "/" + this.touchpoint);
        }

        // verify the campaigns
        verifyCampaigns();

        // remove the products from stock
        checkAndRemoveProductsFromStock();

        // then we add a new customer transaction for the current purchase
        List<ShoppingCartItem> productsInCart = this.shoppingCart.getItems();
        List<CustomerTransactionShoppingCartItem> productsInCartForTransaction = productsInCart
                .stream()
                .map(si -> new CustomerTransactionShoppingCartItem(si.getErpProductId(),si.getUnits(),si.isCampaign()))
                .collect(Collectors.toList());
        CustomerTransaction transaction = new CustomerTransaction(this.customer, this.touchpoint,
                productsInCartForTransaction);
        transaction.setCompleted(true);
        customerTracking.createTransaction(transaction);

        logger.info("purchase(): done.\n");
    }

    /*
     * TODO PAT2: complete the method implementation in your server-side component for shopping / purchasing
     */
    private void checkAndRemoveProductsFromStock() throws ShoppingException {
        logger.info("checkAndRemoveProductsFromStock");

        long pointOfSaleId = this.touchpoint.getErpPointOfSaleId();

        for (ShoppingCartItem item : this.shoppingCart.getItems()) {

            // ermitteln Sie das AbstractProduct für das gegebene ShoppingCartItem über dessen erpProductId und die ProductCRUD bean
            AbstractProduct product = this.productCRUD.readProduct(item.getErpProductId());

            if (item.isCampaign()) {
                this.campaignTracking.purchaseCampaignAtTouchpoint(item.getErpProductId(), this.touchpoint,
                        item.getUnits());

                // bei einer Kampagne über die ProductBundle Objekte des Campaign Objekts iterieren
                Campaign campaign = (Campaign) product;
                for (ProductBundle bundle : campaign.getBundles()) {
                    IndividualisedProductItem bundleProduct = bundle.getProduct();
                    // Anzahl im Bundle multipliziert mit der Anzahl der Kampagne im Warenkorb
                    int requiredUnits = bundle.getUnits() * item.getUnits();
                    checkAndRemove(bundleProduct, pointOfSaleId, requiredUnits);
                }
            } else {
                // kein Kampagnenprodukt: das Produkt selbst in der Anzahl item.getUnits() entfernen
                IndividualisedProductItem individualProduct = (IndividualisedProductItem) product;
                checkAndRemove(individualProduct, pointOfSaleId, item.getUnits());
            }

        }
    }

    /*
     * überprüft die Verfügbarkeit des Produkts am Point of Sale und entfernt es, falls verfügbar,
     * andernfalls wird eine ShoppingException mit dem Grund STOCK_EXCEEDED geworfen
     */
    private void checkAndRemove(IndividualisedProductItem product, long pointOfSaleId, int requiredUnits)
            throws ShoppingException {
        int unitsOnStock = this.stockSystem.getUnitsOnStock(product, pointOfSaleId);
        if (unitsOnStock < requiredUnits) {
            throw new ShoppingException(ShoppingException.ShoppingSessionExceptionReason.STOCK_EXCEEDED,
                    "cannot remove " + requiredUnits + " units of product " + product
                            + " from stock of pointOfSale " + pointOfSaleId + "! Only " + unitsOnStock
                            + " units are available.");
        }
        this.stockSystem.removeFromStock(product, pointOfSaleId, requiredUnits);
    }

    @Override
    public void purchaseCartAtTouchpointForCustomer(PurchaseDTO purchaseDTO) throws ShoppingException {
        this.customer = customerCRUD.readCustomer(purchaseDTO.getCustomerId());
        this.touchpoint = touchpointAccess.readTouchpoint(purchaseDTO.getTouchpointId());
        this.shoppingCart = new ShoppingCartEntity();
        shoppingCartService.getItems(purchaseDTO.getShoppingCartId())
                .forEach(item -> {
                    this.shoppingCart.addItem(new ShoppingCartItem(item.getErpProductId(), item.getUnits(), item.isCampaign()));
                });

        purchase();
    }
}