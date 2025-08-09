# Apache CXF Rest

Proyecto básico de ejemplo de Jakarta RESTful Web Services 3.1 usando Apache CXF. 

## Tecnologías 

- Java 21
- Apache CXF
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

