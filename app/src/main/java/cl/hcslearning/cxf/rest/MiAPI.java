package cl.hcslearning.cxf.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class MiAPI extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> clases = new HashSet<>();
        clases.add( ProductoService.class );

        return clases;
    }
}
