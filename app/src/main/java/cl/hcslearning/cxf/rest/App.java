package cl.hcslearning.cxf.rest;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

public class App {

    public static void main(String[] args) {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();

        sf.setApplication( new MiAPI() );

        //sf.setResourceClasses(ProductoService.class);
        //sf.setResourceProvider(ProductoService.class,
        //   new SingletonResourceProvider(new ProductoService()));

        sf.setAddress("http://localhost:9000");
        sf.create();
    }
}
