package eu.zuinnote.example.springwebdemo.controller;

import eu.zuinnote.example.springwebdemo.order.Order;
import eu.zuinnote.example.springwebdemo.order.OrderRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    private OrderRepository orders;

    public OrderController(OrderRepository orders) {
        this.orders = orders;
    }

    @GetMapping("/order/{id}")
    public Order getOrder(@PathVariable UUID id) {
        return orders.findById(id);
    }

    @GetMapping("/order")
    public Page<Order> getAllOrders(Pageable pageable) {
        return orders.findAll(pageable);
    }
}
