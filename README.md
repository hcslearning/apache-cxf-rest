# Apache CXF Rest

Proyecto básico de ejemplo de Jakarta RESTful Web Services 3.1 usando Apache CXF. 

## Tecnologías 

- Java 21
- Apache CXF 4.1.3
- Jakarta RESTful Web Services 3.1
- Gradle 

## Ejecución

Para iniciar el proyecto:

```bash
./gradlew run 
```

## Pruebas 

### Recupera todos los productos
```
curl -i -X GET http://localhost:9000/api/producto
```

### Recupera un producto por ID
```
curl -i -X GET http://localhost:9000/api/producto/2
```

### Agrega un producto nuevo

```
curl -i -X POST http://localhost:9000/api/producto \
  -H "Content-Type: application/json" \
  -d '{
        "id": 4,
        "nombre": "Cinturón de cuero",
        "precio": 12990
      }'

```

### Edita un producto

```
curl -i -X PUT http://localhost:9000/api/producto/4 \
  -H "Content-Type: application/json" \
  -d '{
        "id": 4,
        "nombre": "Cinturón de cuero genuino",
        "precio": 14990
      }'
```

### Elimina un producto

```
curl -i -X DELETE http://localhost:9000/api/producto/4
```

## Explicación Proyecto

### Dependencias

```
[versions]
cxf           = "4.1.3"
logback       = "1.5.18"
yasson        = "3.0.4"
jax-rs        = "3.1.0"

[libraries]
apache-cxf-core    = { module = "org.apache.cxf:cxf-core",                     version.ref = "cxf" }
apache-cxf-jaxrs   = { module = "org.apache.cxf:cxf-rt-frontend-jaxrs",        version.ref = "cxf"}
apache-cxf-rs-ext  = { module = "org.apache.cxf:cxf-rt-rs-extension-providers",version.ref = "cxf" }
eclipse-yasson     = { module = "org.eclipse:yasson",                          version.ref = "yasson" }
apache-cxf-http    = { module = "org.apache.cxf:cxf-rt-transports-http",       version.ref = "cxf"}
apache-cxf-jetty   = { module = "org.apache.cxf:cxf-rt-transports-http-jetty", version.ref = "cxf"}
apache-cxf-logging = { module = "org.apache.cxf:cxf-rt-features-logging",      version.ref = "cxf"}
logback-core       = { module = "ch.qos.logback:logback-core",                 version.ref = "logback" }
logback-classic    = { module = "ch.qos.logback:logback-classic",              version.ref = "logback" }
```

- **CXF Core**: El núcleo del proyecto CXF, sin él no se podría hacer nada. 
- **CXF JAX-RS Frontend**: Este artefacto provee el soporte para la construcción y despliegue de servicios web de tipo REST sobre Apache CXF.
- **CXF Extension Providers**: Es un adaptador de CXF que permite la detección de implementaciones externas en el flujo de JAX-RS. 
- **Yasson**: Implementación de referencia de la especificación Jakarta JSON Binding creada por la fundación Eclipse. 
- **Transport HTTP Jetty**: Implementación de HTTP de CXF usando el servidor Jetty. 
- **Logback**: Es una librería de Logging que se integra sin problemas con SL4J. 

### Código

Proyecto hace uso de clase SeBootstrap para su ejecución, esta clase es parte del estándar de Jakarta RESTful Web Services. Se utiliza Apache CXF como implementación de la especificación. En este proyecto se agrega artefacto **_cxf-rt-transports-http-jetty_** para que Jetty funcione como el servidor web. 

```java
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
```

Luego, la clase MiAPI extiende de Application, permite configurar el path raíz, además permite especificar las clases configuradas como recurso para los servicios web REST. 

```java
@ApplicationPath("/api")
public class MiAPI extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> clases = new HashSet<>();
        clases.add( ProductoService.class );
        return clases;
    }
}
```

Finalmente, el la clase de ejemplo con el recurso Producto.

```java
@Path("/producto")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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

```