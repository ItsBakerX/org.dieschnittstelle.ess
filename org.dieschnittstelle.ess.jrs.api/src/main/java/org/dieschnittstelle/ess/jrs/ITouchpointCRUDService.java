package org.dieschnittstelle.ess.jrs;

import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/touchpoints")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public interface ITouchpointCRUDService {

	//GET /touchpoints -> Liste von TP im JSON Format
	@GET
	List<StationaryTouchpoint> readAllTouchpoints();

	//GET /touchpoints/${id} -> ein TP im JSON Format
	@GET
	@Path("/{touchpointId}")
	StationaryTouchpoint readTouchpoint(@PathParam("touchpointId") long id);

	// POST /touchpoints mit TP als JSON im Request Body -> TP im JSON Format
	@POST
	StationaryTouchpoint createTouchpoint(StationaryTouchpoint touchpoint);

	// DELETE /touchpoints/${id} -> boolean Wert
	@DELETE
	@Path("/{touchpointId}")
	boolean deleteTouchpoint(@PathParam("touchpointId") long id);
		
	/*
	 * TODO JRS1: add a new annotated method for using the updateTouchpoint functionality of TouchpointCRUDExecutor and implement it
	 */

}
