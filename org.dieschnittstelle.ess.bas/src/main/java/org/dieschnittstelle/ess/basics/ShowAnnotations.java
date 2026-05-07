package org.dieschnittstelle.ess.basics;


import org.dieschnittstelle.ess.basics.annotations.AnnotatedStockItemBuilder;
import org.dieschnittstelle.ess.basics.annotations.DisplayAs;
import org.dieschnittstelle.ess.basics.annotations.StockItemProxyImpl;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.dieschnittstelle.ess.basics.reflection.ReflectedStockItemBuilder.getAccessorNameForField;
import static org.dieschnittstelle.ess.utils.Utils.*;

public class ShowAnnotations {

	public static void main(String[] args) {
		// we initialise the collection
		StockItemCollection collection = new StockItemCollection(
				"stockitems_annotations.xml", new AnnotatedStockItemBuilder());
		// we load the contents into the collection
		collection.load();

		for (IStockItem consumable : collection.getStockItems()) {
			showAttributes(((StockItemProxyImpl)consumable).getProxiedObject());
		}

		// we initialise a consumer
		Consumer consumer = new Consumer();
		// ... and let them consume
		consumer.doShopping(collection.getStockItems());
	}

	/*
	 * TODO BAS2
	 */
	private static void showAttributes(Object instance) {
		show("class is: " + instance.getClass().getSimpleName());
		Class klass = instance.getClass();
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(klass.getSimpleName()).append(" ");

		try {

			// TODO BAS2: create a string representation of instance by iterating
			//  over the object's attributes / fields as provided by its class
			//  and reading out the attribute values. The string representation
			//  will then be built from the field names and field values.
			//  Note that only read-access to fields via getters or direct access
			//  is required here.
			for (Field field : klass.getDeclaredFields()) {
				String getterName = getAccessorNameForField("get", field.getName());
				Method getter = klass.getMethod(getterName);
				//invoke returns the value of a field with the getter method
				var value = getter.invoke(instance);
				var attr = field.getName();
				// TODO BAS3: if the new @DisplayAs annotation is present on a field,
				//  the string representation will not use the field's name, but the name
				//  specified in the the annotation. Regardless of @DisplayAs being present
				//  or not, the field's value will be included in the string representation.
				DisplayAs displayAs = field.getAnnotation(DisplayAs.class);
				if (displayAs != null) {
					attr = displayAs.value();
				}
				sb.append(attr).append(":").append(value).append(", ");

			}
			//remove the last comma and space
			sb.setLength(sb.length()-2);
			sb.append("}");
			show(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("showAnnotations(): exception occurred: " + e,e);
		}

	}

}
