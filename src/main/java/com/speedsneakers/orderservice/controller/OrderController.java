package com.speedsneakers.orderservice.controller;

import com.speedsneakers.orderservice.model.dto.OrderDto;
import com.speedsneakers.orderservice.model.request.OrderRequestModel;
import com.speedsneakers.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para manejar las solicitudes relacionadas con las órdenes.
 */
@RestController
@Slf4j
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Constructor del controlador de órdenes.
     *
     * @param orderService Servicio de órdenes.
     */
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Crea una nueva orden.
     *
     * @param orderRequest Datos de la orden a crear.
     * @return Detalles de la orden creada.
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderRequestModel orderRequest) {

        OrderDto order = orderService.createOrder(orderRequest);
        log.info("Orden creada con éxito: {}", order);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Obtiene una orden por su ID.
     *
     * @param id ID de la orden a obtener.
     * @return Detalles de la orden.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable String id) {

        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
}
