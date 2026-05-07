package org.dieschnittstelle.ess.jrs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

import java.util.List;

/*
 * UE JRS2: 
 * deklarieren Sie hier Methoden fuer:
 * - die Erstellung eines Produkts
 * - das Auslesen aller Produkte
 * - das Auslesen eines Produkts
 * - die Aktualisierung eines Produkts
 * - das Loeschen eines Produkts
 * und machen Sie diese Methoden mittels JAX-RS Annotationen als WebService verfuegbar
 */

/*
 * TODO JRS3: aendern Sie Argument- und Rueckgabetypen der Methoden von IndividualisedProductItem auf AbstractProduct
 */

	@Path("/products")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
public interface IProductCRUDService {

	@POST
	public AbstractProduct createProduct(AbstractProduct prod);

	@GET
	public List<AbstractProduct> readAllProducts();

	@PUT
	@Path("/{id}")
	public AbstractProduct updateProduct(@PathParam("id") long id,
	                                     AbstractProduct update);

	@DELETE
	@Path("/{id}")
	boolean deleteProduct(@PathParam("id")long id);

	@GET
	@Path("/{id}")
	public AbstractProduct readProduct(@PathParam("id") long id);
			
}
