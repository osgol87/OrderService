package com.speedsneakers.orderservice.service;

import com.speedsneakers.orderservice.client.ProductClient;
import com.speedsneakers.orderservice.exception.OrderNotFoundException;
import com.speedsneakers.orderservice.model.dto.OrderDto;
import com.speedsneakers.orderservice.model.dto.OrderItemDto;
import com.speedsneakers.orderservice.model.dto.ProductResponseDto;
import com.speedsneakers.orderservice.model.entity.Order;
import com.speedsneakers.orderservice.model.entity.OrderItem;
import com.speedsneakers.orderservice.model.entity.OrderStatus;
import com.speedsneakers.orderservice.model.request.OrderItemRequestModel;
import com.speedsneakers.orderservice.model.request.OrderRequestModel;
import com.speedsneakers.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementación del servicio de ordenes.
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    /**
     * Repositorio de ordenes
     */
    private final OrderRepository orderRepository;

    /**
     * Cliente para comunicarse con el servicio de productos.
     */
    private final ProductClient productClient;

    /**
     * Constructor del servicio de ordenes
     * @param orderRepository Repositorio de ordenes
     * @param productClient Cliente para comunicarse con el servicio de productos
     */
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductClient productClient) {

        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    /**
     * Crea una nueva orden.
     *
     * @param orderRequest Datos de la orden a crear.
     * @return Detalles de la orden.
     */
    @Override
    @Transactional
    public OrderDto createOrder(OrderRequestModel orderRequest) {

        log.info("Creando una nueva orden con los datos: {}", orderRequest);

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestModel itemRequest : orderRequest.getOrderItems()) {

            // Validamos que el producto exista en el servicio de productos.
            ProductResponseDto productResponse = productClient.getProductById(itemRequest.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPricePerUnit(productResponse.getPrice());

            BigDecimal subtotal = totalAmount.add(
                    productResponse.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
            );

            orderItem.setSubtotal(subtotal);
            
            // Centralizamos la lógica de la relación en un solo metodo.
            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);

        orderRepository.save(order);

        return convertToDto(order);
    }

    /**
     * Obtiene una orden por su identificador.
     *
     * @param id Identificador de la orden.
     * @return Detalles de la orden.
     */
    @Override
    public OrderDto getOrderById(String id) {

        if (!StringUtils.hasLength(id)) {
            throw new IllegalArgumentException("El ID de la orden no puede estar vacío");
        }

        Optional<Order> optionalOrder = orderRepository.findById(Long.valueOf(id));

        if (optionalOrder.isEmpty()) {
            log.warn("Orden con ID {} no encontrada", id);
            throw new OrderNotFoundException(id);
        }

        return convertToDto(optionalOrder.get());
    }

    /**
     * Convierte una entidad Order a un DTO OrderDto.
     *
     * @param order Entidad Order a convertir.
     * @return OrderDto con los datos de la orden.
     */
    private OrderDto convertToDto(Order order) {

        OrderDto orderDto = new OrderDto(
                order.getId(),
                order.getOrderDate(),
                order.getStatus().toString(),
                order.getTotalAmount()
        );

        for (OrderItem item : order.getOrderItems()) {
            OrderItemDto itemRequest = new OrderItemDto(
                    item.getId(),
                    item.getProductId(),
                    item.getQuantity(),
                    item.getPricePerUnit(),
                    item.getSubtotal()
            );
            // Añadimos el item a la orden DTO
            orderDto.addOrderItem(itemRequest);
        }

        log.info("Orden creada con éxito: {}", orderDto);
        return orderDto;
    }
}
