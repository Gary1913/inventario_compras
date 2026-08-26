package com.novatech.inventario.service;

import com.novatech.inventario.dto.MovimientoStockDTO;
import com.novatech.inventario.exception.RecursoNoEncontradoException;
import com.novatech.inventario.exception.StockInsuficienteException;
import com.novatech.inventario.model.Categoria;
import com.novatech.inventario.model.Producto;
import com.novatech.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public ProductoService(ProductoRepository productoRepository, CategoriaService categoriaService) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id " + id));
    }

    public Producto crear(Producto producto, Long categoriaId) {
        Categoria categoria = categoriaService.obtenerPorId(categoriaId);
        producto.setCategoria(categoria);
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto datos, Long categoriaId) {
        Producto producto = obtenerPorId(id);
        producto.setNombre(datos.getNombre());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        if (categoriaId != null) {
            producto.setCategoria(categoriaService.obtenerPorId(categoriaId));
        }
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        Producto producto = obtenerPorId(id);
        productoRepository.delete(producto);
    }

    /**
     * Registra un movimiento de stock (entrada o salida).
     *
     * NOTA HISTORICA (para la demo del hotfix):
     * En la version 1.0.0 esta validacion NO existia, lo que permitia que una
     * SALIDA con cantidad mayor al stock disponible dejara el stock en negativo.
     * El fix se aplico en la rama hotfix/1.0.1 agregando la validacion de abajo.
     */
    public Producto registrarMovimiento(Long id, MovimientoStockDTO movimiento) {
        Producto producto = obtenerPorId(id);

        if (movimiento.getTipo() == MovimientoStockDTO.Tipo.ENTRADA) {
            producto.setStock(producto.getStock() + movimiento.getCantidad());
        } else {
            if (movimiento.getCantidad() > producto.getStock()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente: disponible " + producto.getStock()
                                + ", se intento retirar " + movimiento.getCantidad());
            }
            producto.setStock(producto.getStock() - movimiento.getCantidad());
        }

        return productoRepository.save(producto);
    }
}
