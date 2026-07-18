package org.dieschnittstelle.ess.mip.client.shopping;

import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.entities.shopping.ShoppingCartItem;
import org.dieschnittstelle.ess.mip.client.apiclients.ServiceProxyFactory;
import org.dieschnittstelle.ess.mip.client.apiclients.ShoppingCartClient;
import org.dieschnittstelle.ess.mip.components.shopping.api.PurchaseService;
import org.dieschnittstelle.ess.mip.components.shopping.api.ShoppingException;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.Customer;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;

public class PurchaseServiceClient implements ShoppingBusinessDelegate {

	protected static Logger logger = org.apache.logging.log4j.LogManager
			.getLogger(PurchaseServiceClient.class);

	private PurchaseService purchaseServiceProxy;
	private ShoppingCartClient shoppingCartClient;
	private AbstractTouchpoint touchpoint;
	private Customer customer;

	public PurchaseServiceClient() {
		purchaseServiceProxy = ServiceProxyFactory.getInstance().getProxy(PurchaseService.class);
		try {
			this.shoppingCartClient = new ShoppingCartClient();
		} catch (Exception e) {
            throw new RuntimeException("Got exception: " + e);
        }
    }

	@Override
	public void setTouchpoint(AbstractTouchpoint touchpoint) {
		this.touchpoint = touchpoint;
	}

	@Override
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public void addProduct(AbstractProduct product, int units) {
		this.shoppingCartClient.addItem(new ShoppingCartItem(product.getId(), units, product instanceof Campaign));
	}

	@Override
	public void purchase() throws ShoppingException {
		this.purchaseServiceProxy.purchaseCartAtTouchpointForCustomer(new PurchaseService.PurchaseDTO(
				shoppingCartClient.getShoppingCartEntityId(), touchpoint.getId(), customer.getId()
		));
	}

}
