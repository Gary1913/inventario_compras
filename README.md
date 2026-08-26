# Sistema de Inventario — NovaTech Solutions

API REST en Spring Boot para gestionar el inventario de productos: alta/baja/edición de
productos y categorías, y control de stock (entradas y salidas).

## Modelo de datos

**Producto**: `id`, `nombre`, `precio`, `categoria`, `stock`
**Categoria**: `id`, `nombre`

## Requisitos

- Java 17+
- Maven 3.9+

## Ejecutar

```bash
mvn spring-boot:run
```

La app levanta en `http://localhost:8080`. Consola H2 disponible en `/h2-console`
(JDBC URL: `jdbc:h2:mem:inventariodb`, usuario `sa`, sin password).

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productos` | Listar productos |
| GET | `/api/productos/{id}` | Obtener un producto |
| POST | `/api/productos` | Crear producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |
| PATCH | `/api/productos/{id}/stock` | Registrar movimiento de stock (`ENTRADA`/`SALIDA`) |
| GET/POST/DELETE | `/api/categorias` | CRUD de categorías |

Ejemplo de creación de producto:

```json
POST /api/productos
{
  "nombre": "Monitor 24 pulgadas",
  "precio": 199.99,
  "stock": 12,
  "categoriaId": 1
}
```

Ejemplo de movimiento de stock:

```json
PATCH /api/productos/1/stock
{
  "tipo": "SALIDA",
  "cantidad": 5
}
```

## Pruebas

```bash
mvn test
```

## Estrategia de control de versiones

Ver [`GIT_WORKFLOW.md`](./GIT_WORKFLOW.md) para el detalle de la estrategia de ramas
(Git Flow), la convención de commits y la simulación del bug crítico resuelto vía hotfix.
