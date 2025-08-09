package cl.hcslearning.cxf.rest;

import jakarta.ws.rs.SeBootstrap;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

public class App {

    public static void main(String[] args) {
        jakartaRsWsStart();
        // cxfStart();
    }

    private static void jakartaRsWsStart() {
        final SeBootstrap.Configuration conf = SeBootstrap.Configuration
                .builder()
                .protocol("http")
                .host("localhost")
                .port(9000)
                .rootPath("/")
                .build()
        ;

        SeBootstrap
                .start(new MiAPI(), conf)
                .toCompletableFuture()
                .join()
        ;
    }

    private static void cxfStart() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(ProductoService.class);
        sf.setResourceProvider(ProductoService.class,
        new SingletonResourceProvider(new ProductoService()));
        sf.setAddress("http://localhost:9000");
        sf.create();
    }
}
