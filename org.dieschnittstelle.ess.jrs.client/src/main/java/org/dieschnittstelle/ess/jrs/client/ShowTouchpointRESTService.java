package org.dieschnittstelle.ess.jrs.client;

import java.util.List;

//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.cfg.MapperConfig;
//import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.Address;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.ess.jrs.ITouchpointCRUDService;
import org.dieschnittstelle.ess.jrs.client.jackson.LaissezFairePolymorphicJacksonProvider;
import org.dieschnittstelle.ess.utils.Utils;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import static org.dieschnittstelle.ess.utils.Utils.*;

public class ShowTouchpointRESTService {

	protected static Logger logger = org.apache.logging.log4j.LogManager
			.getLogger(ShowTouchpointRESTService.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// for demo purposes: control whether we are accessing the synchronous or the asynchronous service
		boolean async = false;

		/*
		 * create a client for the web service passing the interface
		 */
		Client client = ClientBuilder.newBuilder()
				.build()
				// use our custom jackson provider that is less restrictive if it comes to dealing with polymorphic types
				// handled using @JsonTypeInfo
				.register(LaissezFairePolymorphicJacksonProvider.class);

		ResteasyWebTarget target = (ResteasyWebTarget)client.target("http://localhost:8080/api/" + (async ? "async/" : ""));
		ITouchpointCRUDService serviceProxy = target.proxy(ITouchpointCRUDService.class);

		show("serviceProxy: " + serviceProxy + " of class: " + serviceProxy.getClass());

		// 1) read out all touchpoints
		List<StationaryTouchpoint> touchpoints = serviceProxy.readAllTouchpoints();
		logger.info("read touchpoints: " + touchpoints);

		// 2) delete the touchpoint after next console input
		if (touchpoints != null && touchpoints.size() > 0) {
			Utils.step();

			StationaryTouchpoint tp = touchpoints.get(0);
			serviceProxy.deleteTouchpoint(tp.getId());
			logger.info("deleted touchpoint: " + tp);
		}
		else {
			logger.warn("no touchpoints available for deletion...");
		}

		// 3) wait for input and create a new touchpoint
		Utils.step();

		Address addr = new Address("Luxemburger Strasse", "10", "13353",
				"Berlin");
		StationaryTouchpoint tp = new StationaryTouchpoint(-1,
				"BHT Verkaufsstand", addr);

		tp = (StationaryTouchpoint)serviceProxy.createTouchpoint(tp);
		logger.info("created touchpoint: " + tp);

		/*
		 * 4) wait for input and...
		 */
		Utils.step();
		// change the name
		tp.setName("BHT Mensa");

		/*
		 * UE JRS1: add a call to the update method, passing tp
		 */
		tp = serviceProxy.updateTouchpoint(tp.getId(), tp);
		logger.info("renamed touchpoint with id " + tp.getId() + " to " + tp.getName());
		show("TestTouchpointRESTService: done.\n");

	}

}
