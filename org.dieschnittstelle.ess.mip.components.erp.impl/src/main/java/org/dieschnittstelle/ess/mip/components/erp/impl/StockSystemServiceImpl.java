package org.dieschnittstelle.ess.mip.components.erp.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystemService;
import org.dieschnittstelle.ess.mip.components.erp.api.dto.StockItemDTO;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.mip.components.erp.crud.impl.EntityManagerProvider;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;

@ApplicationScoped
@Transactional
@Logged
public class StockSystemServiceImpl implements StockSystemService {

    @Inject
    private StockSystem stockSystem;

    @Inject
    private ProductCRUD productCRUD;

    @Override
    public void addToStock(StockItemDTO stockItemDTO) {
        IndividualisedProductItem prod = (IndividualisedProductItem) productCRUD.readProduct(stockItemDTO.getProdId());
        stockSystem.addToStock(prod, stockItemDTO.getPosId(), stockItemDTO.getUnits());
    }

    @Override
    public void removeFromStock(StockItemDTO stockItemDTO) {
        IndividualisedProductItem prod = (IndividualisedProductItem) productCRUD.readProduct(stockItemDTO.getProdId());
        stockSystem.removeFromStock(prod, stockItemDTO.getPosId(), stockItemDTO.getUnits());
    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        if(pointOfSaleId == 0){
            return stockSystem.getAllProductsOnStock();
        }else {
            return stockSystem.getProductsOnStock(pointOfSaleId);
        }
    }

    @Override
    public int getUnitsOnStock(long productId, long pointOfSaleId) {
        IndividualisedProductItem prod = (IndividualisedProductItem) productCRUD.readProduct(productId);
        if (pointOfSaleId == 0) {
            return stockSystem.getTotalUnitsOnStock(prod);
        } else {
            return stockSystem.getUnitsOnStock(prod, pointOfSaleId);
        }
    }

    @Override
    public List<Long> getPointsOfSale(long productId) {
        IndividualisedProductItem prod = (IndividualisedProductItem) productCRUD.readProduct(productId);
        return stockSystem.getPointsOfSale(prod);
    }
}
