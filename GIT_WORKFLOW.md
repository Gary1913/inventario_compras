# Estrategia de control de versiones — NovaTech Solutions

Este documento describe, paso a paso, cómo reprodujimos un flujo de trabajo profesional
tipo **Git Flow** para el desarrollo del sistema de inventario.

## 1. Ramas

```
main        → versiones estables, cada merge se etiqueta con un tag
develop     → integración continua de features
feature/*   → una rama por funcionalidad, nace y muere en develop
release/*   → congela una versión antes de pasar a main
hotfix/*    → corrige un bug crítico directamente sobre main
```

## 2. Inicialización del repositorio

```bash
cd novatech-inventario
git init
git add .
git commit -m "chore: inicializar proyecto Spring Boot con estructura base"
git branch -M main
git checkout -b develop
git push -u origin main
git push -u origin develop
```

## 3. Desarrollo por features

Para cada funcionalidad, se crea una rama desde `develop`, se hacen commits pequeños
y se integra de vuelta vía Pull Request.

### feature/categorias
```bash
git checkout develop
git checkout -b feature/categorias
git add src/main/java/com/novatech/inventario/model/Categoria.java
git commit -m "feat: agregar entidad Categoria"
git add src/main/java/com/novatech/inventario/repository/CategoriaRepository.java
git commit -m "feat: agregar repositorio de Categoria"
git add src/main/java/com/novatech/inventario/service/CategoriaService.java
git commit -m "feat: agregar servicio de Categoria con CRUD basico"
git add src/main/java/com/novatech/inventario/controller/CategoriaController.java
git commit -m "feat: exponer endpoints REST de Categoria"
git push -u origin feature/categorias
# Abrir PR feature/categorias -> develop, revisar y mergear
```

### feature/crud-productos
```bash
git checkout develop
git pull
git checkout -b feature/crud-productos
git add src/main/java/com/novatech/inventario/model/Producto.java
git commit -m "feat: agregar entidad Producto"
git add src/main/java/com/novatech/inventario/repository/ProductoRepository.java
git commit -m "feat: agregar repositorio de Producto"
git add src/main/java/com/novatech/inventario/exception/RecursoNoEncontradoException.java
git commit -m "feat: agregar excepcion para recursos no encontrados"
git add src/main/java/com/novatech/inventario/service/ProductoService.java
git commit -m "feat: implementar CRUD de Producto en el servicio"
git push -u origin feature/crud-productos
# PR feature/crud-productos -> develop
```

### feature/api-rest-inventario
```bash
git checkout develop
git pull
git checkout -b feature/api-rest-inventario
git add src/main/java/com/novatech/inventario/dto/ProductoDTO.java
git commit -m "feat: agregar ProductoDTO para exponer datos vía API"
git add src/main/java/com/novatech/inventario/controller/ProductoController.java
git commit -m "feat: exponer endpoints REST CRUD de Producto"
git add src/main/java/com/novatech/inventario/exception/GlobalExceptionHandler.java
git commit -m "feat: manejar errores de forma centralizada en la API"
git push -u origin feature/api-rest-inventario
# PR feature/api-rest-inventario -> develop
```

### feature/validaciones-precio
```bash
git checkout develop
git pull
git checkout -b feature/validaciones-precio
# aplicar anotaciones @NotNull/@DecimalMin sobre precio en Producto y ProductoDTO
git add src/main/java/com/novatech/inventario/model/Producto.java
git add src/main/java/com/novatech/inventario/dto/ProductoDTO.java
git commit -m "feat: validar que el precio sea mayor a 0"
git push -u origin feature/validaciones-precio
# PR feature/validaciones-precio -> develop
```

### feature/gestion-stock
```bash
git checkout develop
git pull
git checkout -b feature/gestion-stock
git add src/main/java/com/novatech/inventario/dto/MovimientoStockDTO.java
git commit -m "feat: agregar DTO de movimiento de stock (entrada/salida)"
git add src/main/java/com/novatech/inventario/service/ProductoService.java
git commit -m "feat: registrar movimientos de entrada y salida de stock"
git add src/main/java/com/novatech/inventario/controller/ProductoController.java
git commit -m "feat: exponer endpoint PATCH para movimientos de stock"
git push -u origin feature/gestion-stock
# PR feature/gestion-stock -> develop
```

> Nota: en esta primera versión, a propósito, **no** se incluye la validación de
> `cantidad > stock` (ver sección 5, esa es la que se "descubre" como bug en producción).

## 4. Preparar la release 1.0.0

```bash
git checkout develop
git pull
git checkout -b release/1.0.0
# ajustes finales: version en pom.xml, README, application.properties
git add pom.xml README.md
git commit -m "chore: preparar release 1.0.0"
git push -u origin release/1.0.0

# Merge a main
git checkout main
git merge --no-ff release/1.0.0 -m "release: version 1.0.0"
git tag -a v1.0.0 -m "Primera version funcional del sistema de inventario"
git push origin main --tags

# Merge de vuelta a develop
git checkout develop
git merge --no-ff release/1.0.0 -m "merge: incorporar release 1.0.0 a develop"
git push origin develop

# Limpieza
git branch -d release/1.0.0
git push origin --delete release/1.0.0
```

## 5. Bug crítico y hotfix (v1.0.1)

**Bug reportado en producción:** al registrar una salida de stock con una cantidad
mayor a la disponible, el stock queda en negativo (no existía validación).

```bash
git checkout main
git checkout -b hotfix/1.0.1

# Aplicar el fix en ProductoService.registrarMovimiento():
# validar que movimiento.getCantidad() no supere producto.getStock()
# y lanzar StockInsuficienteException en caso contrario.
git add src/main/java/com/novatech/inventario/exception/StockInsuficienteException.java
git add src/main/java/com/novatech/inventario/service/ProductoService.java
git add src/main/java/com/novatech/inventario/exception/GlobalExceptionHandler.java
git commit -m "fix: prevenir stock negativo al registrar una salida"

git add src/test/java/com/novatech/inventario/ProductoServiceStockTest.java
git commit -m "test: cubrir el caso de salida mayor al stock disponible"

git add pom.xml
git commit -m "chore: bump version a 1.0.1"

# Merge a main
git checkout main
git merge --no-ff hotfix/1.0.1 -m "hotfix: corregir stock negativo (v1.0.1)"
git tag -a v1.0.1 -m "Hotfix: prevenir stock negativo en salidas"
git push origin main --tags

# Merge a develop para no perder el fix
git checkout develop
git merge --no-ff hotfix/1.0.1 -m "merge: incorporar hotfix 1.0.1 a develop"
git push origin develop

git branch -d hotfix/1.0.1
git push origin --delete hotfix/1.0.1
```

## 6. Resultado esperado en el historial

```bash
git log --oneline --graph --all
```

Debe mostrarse:
- `develop` con commits de todas las features, adelante o igual a `main`.
- Dos tags en `main`: `v1.0.0` y `v1.0.1`.
- El hotfix presente tanto en `main` como en `develop`.
- Ninguna rama `feature/*` mergeada directamente a `main`.

## 7. Puntos clave para la defensa técnica

- **¿Por qué el hotfix nace de `main` y no de `develop`?** Porque `develop` puede
  contener features a medio terminar; `main` es lo único garantizado como lo que
  está corriendo en producción.
- **¿Por qué mergear el hotfix también a `develop`?** Para que el fix no se pierda
  cuando se genere la siguiente release.
- **¿Por qué `--no-ff`?** Para conservar en el historial la evidencia de que existió
  una rama de release/hotfix, en vez de aplanar los commits.
- **¿Por qué tags anotados (`-a`)?** Guardan autor, fecha y mensaje — trazabilidad
  real de qué se entregó y cuándo, útil para auditoría.
