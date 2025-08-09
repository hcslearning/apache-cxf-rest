package cl.hcslearning.cxf.rest;

import jakarta.ws.rs.SeBootstrap;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final SeBootstrap.Configuration conf = SeBootstrap.Configuration
                .builder()
                .protocol("http")
                .host("localhost")
                .port(9000)
                .rootPath("/")
                .build()
        ;

        SeBootstrap.Instance instance = SeBootstrap
                .start(new MiAPI(), conf)
                .toCompletableFuture()
                .get()
        ;

        // Latch para bloquear el main
        CountDownLatch latch = new CountDownLatch(1);

        // Se ejecuta cuando llega señal de apagado (Ctrl+C)
        instance.stopOnShutdown(inst -> {
            System.out.println("Servidor detenido");
            latch.countDown();
        });

        // Bloquea hasta que latch.countDown() sea llamado
        latch.await();
    }

    private static void cxfStart() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();

        sf.setApplication( new MiAPI() );

        //sf.setResourceClasses(ProductoService.class);
        //sf.setResourceProvider(ProductoService.class,
        //   new SingletonResourceProvider(new ProductoService()));

        sf.setAddress("http://localhost:9000");
        sf.create();
    }
}
