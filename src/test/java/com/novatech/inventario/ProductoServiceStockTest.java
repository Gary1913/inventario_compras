package com.novatech.inventario;

import com.novatech.inventario.dto.MovimientoStockDTO;
import com.novatech.inventario.exception.StockInsuficienteException;
import com.novatech.inventario.model.Categoria;
import com.novatech.inventario.model.Producto;
import com.novatech.inventario.repository.ProductoRepository;
import com.novatech.inventario.service.CategoriaService;
import com.novatech.inventario.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductoServiceStockTest {

    @Test
    void noDebePermitirSalidaMayorAlStockDisponible() {
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        CategoriaService categoriaService = Mockito.mock(CategoriaService.class);
        ProductoService service = new ProductoService(repo, categoriaService);

        Producto producto = new Producto("Mouse", 20.0, 5, new Categoria("Electronica"));
        producto.setId(1L);
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(producto));

        MovimientoStockDTO movimiento = new MovimientoStockDTO();
        movimiento.setTipo(MovimientoStockDTO.Tipo.SALIDA);
        movimiento.setCantidad(10);

        assertThrows(StockInsuficienteException.class,
                () -> service.registrarMovimiento(1L, movimiento));
    }

    @Test
    void debePermitirEntradaDeStock() {
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        CategoriaService categoriaService = Mockito.mock(CategoriaService.class);
        ProductoService service = new ProductoService(repo, categoriaService);

        Producto producto = new Producto("Teclado", 50.0, 3, new Categoria("Electronica"));
        producto.setId(2L);
        Mockito.when(repo.findById(2L)).thenReturn(Optional.of(producto));
        Mockito.when(repo.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        MovimientoStockDTO movimiento = new MovimientoStockDTO();
        movimiento.setTipo(MovimientoStockDTO.Tipo.ENTRADA);
        movimiento.setCantidad(7);

        Producto actualizado = service.registrarMovimiento(2L, movimiento);
        assertEquals(10, actualizado.getStock());
    }
}
