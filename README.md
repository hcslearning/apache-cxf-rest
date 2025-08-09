# Apache CXF REST - Ejemplo Básico

Este es un proyecto de ejemplo que demuestra cómo crear servicios RESTful utilizando **Jakarta RESTful Web Services 3.1** junto con **Apache CXF 4.1.3**. El proyecto está desarrollado con **Java 21** y se construye y ejecuta con **Gradle**.

---

## Tecnologías utilizadas

- **Java 21**
- **Apache CXF 4.1.3**
- **Jakarta RESTful Web Services 3.1**
- **Gradle** (para construcción y ejecución)

---

## Cómo ejecutar el proyecto

Para iniciar el servidor REST localmente, ejecuta el siguiente comando en la raíz del proyecto:

```bash
./gradlew run
```

## Pruebas con curl 

A continuación se muestran ejemplos para probar los principales endpoints del API usando `curl`.

### 1. Obtener todos los productos

Recupera la lista completa de productos registrados.

```bash
curl -i -X GET http://localhost:9000/api/producto
```

### 2. Obtener un producto por ID

Recupera un producto específico usando su identificador.

```bash
curl -i -X GET http://localhost:9000/api/producto/2
```

### 3. Agregar un nuevo producto

Crea un nuevo producto enviando los datos en formato JSON.

```bash
curl -i -X POST http://localhost:9000/api/producto \
  -H "Content-Type: application/json" \
  -d '{
        "id": 4,
        "nombre": "Cinturón de cuero",
        "precio": 12990
      }'
```

### 4. Modificar un producto existente

Actualiza los datos de un producto identificado por su ID.

```bash
curl -i -X PUT http://localhost:9000/api/producto/4 \
  -H "Content-Type: application/json" \
  -d '{
        "id": 4,
        "nombre": "Cinturón de cuero genuino",
        "precio": 14990
      }'
```

### 5. Eliminar un producto

Elimina un producto especificando su ID.

```bash
curl -i -X DELETE http://localhost:9000/api/producto/4
```

## Explicación Proyecto

### Dependencias principales

El proyecto utiliza las siguientes dependencias para construir y ejecutar el servicio REST con Apache CXF:

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

### ¿Qué hace cada dependencia?

- **CXF Core**: El componente central de Apache CXF. Proporciona la base para construir servicios web, tanto SOAP como REST. Sin esta dependencia, no se puede usar CXF.

- **CXF JAX-RS Frontend**: Añade soporte para construir servicios RESTful (JAX-RS) usando Apache CXF. Maneja la transformación de recursos Java en endpoints HTTP REST.

- **CXF Extension Providers**: Permite a Apache CXF detectar y utilizar implementaciones externas de proveedores en el flujo de JAX-RS, mejorando la compatibilidad con distintas tecnologías y formatos.

- **Yasson**: Es la implementación oficial de Eclipse para Jakarta JSON Binding (JSON-B). Se encarga de convertir automáticamente objetos Java a JSON y viceversa.

- **Transport HTTP Jetty**: Proporciona un servidor HTTP embebido basado en Jetty para ejecutar el servicio REST sin necesidad de un servidor externo.

- **Logback (Core y Classic)**: Librería de logging moderna y potente, compatible con SLF4J. Se utiliza para registrar eventos, errores e información durante la ejecución de la aplicación.

- **CXF Logging Feature**: Agrega funcionalidades para registrar las peticiones y respuestas HTTP, útil para depuración y monitoreo.


### Código

Este proyecto utiliza la clase `SeBootstrap` para iniciar el servicio REST, que forma parte del estándar **Jakarta RESTful Web Services**. Apache CXF es la implementación utilizada para dicha especificación. Además, se incluye el artefacto **`cxf-rt-transports-http-jetty`** para que el servidor Jetty actúe como servidor HTTP embebido.

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

La clase MiAPI extiende de Application, lo que permite configurar el path raíz para el API REST y registrar las clases que exponen los recursos (endpoints) del servicio.

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

Finalmente, la clase ProductoService define el recurso REST para manejar productos. Usa las anotaciones JAX-RS para definir los endpoints, los métodos HTTP, y los formatos JSON para entrada y salida.

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