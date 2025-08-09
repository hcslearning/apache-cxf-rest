package cl.hcslearning.cxf.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/producto")
@Produces(MediaType.APPLICATION_JSON)
public class ProductoService {

    private final List<Producto> productos = List.of(
            new Producto(1, "Plato comida metálico para mascotas", 5_990),
            new Producto(2, "Balón fútbol", 19_990),
            new Producto(3, "Fundas de Asientos 8 piezas", 49_990)
    );

    @GET
    @Path("/{id}")
    public Producto getProducto(@PathParam("id") long id) {
        return productos
                .stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() ->
                    new WebApplicationException(
                            "Producto no encontrado",
                            Response.Status.NOT_FOUND
                    )
                );
    }

    @GET
    public List<Producto> getProductos() {
        return productos;
    }
}
