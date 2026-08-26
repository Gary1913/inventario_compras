package com.novatech.inventario.controller;

import com.novatech.inventario.dto.MovimientoStockDTO;
import com.novatech.inventario.dto.ProductoDTO;
import com.novatech.inventario.model.Producto;
import com.novatech.inventario.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoDTO> listar() {
        return productoService.listarTodos().stream()
                .map(this::aDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductoDTO obtener(@PathVariable Long id) {
        return aDTO(productoService.obtenerPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO crear(@Valid @RequestBody ProductoDTO dto) {
        Producto producto = new Producto(dto.getNombre(), dto.getPrecio(), dto.getStock(), null);
        return aDTO(productoService.crear(producto, dto.getCategoriaId()));
    }

    @PutMapping("/{id}")
    public ProductoDTO actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        Producto producto = new Producto(dto.getNombre(), dto.getPrecio(), dto.getStock(), null);
        return aDTO(productoService.actualizar(id, producto, dto.getCategoriaId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }

    @PatchMapping("/{id}/stock")
    public ProductoDTO registrarMovimientoStock(@PathVariable Long id,
                                                 @Valid @RequestBody MovimientoStockDTO movimiento) {
        return aDTO(productoService.registrarMovimiento(id, movimiento));
    }

    private ProductoDTO aDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        return dto;
    }
}
