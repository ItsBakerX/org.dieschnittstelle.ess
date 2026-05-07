package org.dieschnittstelle.ess.jrs;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.GenericCRUDExecutor;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

import java.util.List;

/*
 * TODO JRS2: implementieren Sie hier die im Interface deklarierten Methoden
 */

public class ProductCRUDServiceImpl implements IProductCRUDService {

	protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(ProductCRUDServiceImpl.class);

	/**
	 * this accessor will be provided by the ServletContext, to which it is written by the TouchpointServletContextListener
	 */
	private GenericCRUDExecutor<AbstractProduct> productCRUD;

	/**
	 * here we will be passed the context parameters by the resteasy framework. Alternatively @Context
	 * can  be declared on the respective instance attributes. note that the request context is only
	 * declared for illustration purposes, but will not be further used here
	 *
	 * @param servletContext
	 */
	public ProductCRUDServiceImpl(@Context ServletContext servletContext, @Context HttpServletRequest request) {
		logger.info("<constructor>: " + servletContext + "/" + request);
		// read out the dataAccessor
		this.productCRUD = (GenericCRUDExecutor<AbstractProduct>) servletContext.getAttribute("productCRUD");
		logger.debug("read out the productCRUD from the servlet context: " + this.productCRUD);
	}

	@Override
	public AbstractProduct createProduct(
			AbstractProduct prod) {
		return this.productCRUD.createObject(prod);
	}

	@Override
	public List<AbstractProduct> readAllProducts() {
		return (List) this.productCRUD.readAllObjects();
	}

	@Override
	public AbstractProduct updateProduct(long id,
	                                     AbstractProduct update) {
		update.setId(id);
		return this.productCRUD.updateObject(update);
	}

	@Override
	public boolean deleteProduct(long id) {
			return this.productCRUD.deleteObject(id);
	}

	@Override
	public AbstractProduct readProduct(long id) {
		AbstractProduct product = this.productCRUD.readObject(id);

		if (product != null) {
            return product;
        } else {
            throw new NotFoundException("The product with id " + id + " does not exist!");
        }
	}
}
