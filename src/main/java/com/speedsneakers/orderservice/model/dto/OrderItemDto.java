package com.speedsneakers.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    /**
     * Identificador del item de la orden.
     */
    private Long id;

    /**
     * Identificador del producto.
     */
    private Long productId;

    /**
     * Cantidad del producto.
     */
    private Integer quantity;

    /**
     * Precio por unidad del producto.
     */
    private BigDecimal pricePerUnit;

    /**
     * Subtotal del item de la orden (quantity * pricePerUnit).
     */
    private BigDecimal subtotal;

    /**
     * Convierte el item de la orden a una representación de cadena.
     *
     * @return String representation of the OrderItemDto.
     */
    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", pricePerUnit=" + pricePerUnit +
                ", subtotal=" + subtotal +
                '}';
    }
}
