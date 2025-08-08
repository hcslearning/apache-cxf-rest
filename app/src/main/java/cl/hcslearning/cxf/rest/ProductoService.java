package cl.hcslearning.cxf.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/producto")
@Produces(MediaType.APPLICATION_JSON)
public class ProductoService {

    @GET
    @Path("/{id}")
    public Producto getProducto(@PathParam("id") long id) {
        return new Producto(1, "Plato comida metálico para mascotas", 5_990);
    }

}
