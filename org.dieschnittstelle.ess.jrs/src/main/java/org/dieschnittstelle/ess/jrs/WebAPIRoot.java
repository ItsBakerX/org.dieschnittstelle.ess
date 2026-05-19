package org.dieschnittstelle.ess.jrs;

import org.dieschnittstelle.ess.jrs.opi.ProductCRUDServiceOPIImpl;
import org.dieschnittstelle.ess.jrs.opi.TouchpointCRUDServiceOPIImpl;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
@OpenAPIDefinition(info=@Info(title = "WebAPI for JRS, OPI and WSV exercises", version = "0.1"))
public class WebAPIRoot extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        //return new HashSet(Arrays.asList(new Class[]{TouchpointCRUDServiceImpl.class, ProductCRUDServiceImpl.class, TouchpointCRUDServiceImplAsync.class, TouchpointCRUDServiceOPIImpl.class, ProductCRUDServiceOPIImpl.class}));
        return Set.of(TouchpointCRUDServiceImpl.class, ProductCRUDServiceImpl.class);
    }
}
