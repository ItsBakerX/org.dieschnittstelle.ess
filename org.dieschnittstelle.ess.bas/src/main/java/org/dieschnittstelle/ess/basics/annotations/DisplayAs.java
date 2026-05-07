package org.dieschnittstelle.ess.basics.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Die Annotation ist auch zur Laufzeit noch vorhanden und kann per Reflection gelesen werden.
@Retention(RetentionPolicy.RUNTIME)
// Die Annotation darf nur auf Attribute/Felder gesetzt werden.
@Target(ElementType.FIELD)
public @interface DisplayAs {
    String value();
}
