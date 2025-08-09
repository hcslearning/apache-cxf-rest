package cl.hcslearning.cxf.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/producto")
@Produces(MediaType.APPLICATION_JSON)
public class ProductoService {

    private static final List<Producto> productos = new ArrayList<>( List.of(
            new Producto(1, "Plato comida metálico para mascotas", 5_990),
            new Producto(2, "Balón fútbol", 19_990),
            new Producto(3, "Fundas de Asientos 8 piezas", 49_990)
    ));

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

    @POST
    public Response addProducto(Producto nuevo) {
        boolean existe = productos.stream().anyMatch(p -> p.getId() == nuevo.getId());
        if (existe) {
            throw new WebApplicationException("Ya existe un producto con ese ID", Response.Status.CONFLICT);
        }
        productos.add(nuevo);
        return Response.status(Response.Status.CREATED).entity(nuevo).build();
    }

    @PUT
    @Path("/{id}")
    public Producto updateProducto(@PathParam("id") long id, Producto actualizado) {
        Producto existente = getProducto(id);
        existente.setNombre(actualizado.getNombre());
        existente.setPrecio(actualizado.getPrecio());
        return existente;
    }

    // Eliminar un producto
    @DELETE
    @Path("/{id}")
    public Response deleteProducto(@PathParam("id") long id) {
        boolean eliminado = productos.removeIf(p -> p.getId() == id);
        if (!eliminado) {
            throw new WebApplicationException("Producto no encontrado", Response.Status.NOT_FOUND);
        }
        return Response.noContent().build(); // http response 204
    }

}
