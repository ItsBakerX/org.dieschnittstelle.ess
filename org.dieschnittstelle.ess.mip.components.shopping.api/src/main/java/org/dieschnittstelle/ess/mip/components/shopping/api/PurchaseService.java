package org.dieschnittstelle.ess.mip.components.shopping.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

// TODO: PAT1: this is the interface to be provided as a rest service if rest service access is used
@Path("/purchase")
@Consumes({MediaType.APPLICATION_JSON})
@Produces(MediaType.APPLICATION_JSON)
public interface PurchaseService {
	@POST
	void purchaseCartAtTouchpointForCustomer(PurchaseDTO purchaseDTO) throws ShoppingException;

	public static class PurchaseDTO {
		public long shoppingCartId;
		public long touchpointId;
		public long customerId;

		public PurchaseDTO() {
		}

		public PurchaseDTO(long shoppingCartId, long touchpointId, long customerId) {
			this.shoppingCartId = shoppingCartId;
			this.touchpointId = touchpointId;
			this.customerId = customerId;
		}

		public long getShoppingCartId() {
			return shoppingCartId;
		}

		public void setShoppingCartId(long shoppingCartId) {
			this.shoppingCartId = shoppingCartId;
		}

		public long getTouchpointId() {
			return touchpointId;
		}

		public void setTouchpointId(long touchpointId) {
			this.touchpointId = touchpointId;
		}

		public long getCustomerId() {
			return customerId;
		}

		public void setCustomerId(long customerId) {
			this.customerId = customerId;
		}
	}
}
