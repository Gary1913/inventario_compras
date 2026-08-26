package com.novatech.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MovimientoStockDTO {

    public enum Tipo {
        ENTRADA,
        SALIDA
    }

    @NotNull(message = "El tipo de movimiento es obligatorio (ENTRADA o SALIDA)")
    private Tipo tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
