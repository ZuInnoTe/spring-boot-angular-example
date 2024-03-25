package eu.zuinnote.example.springwebdemo.controller;

import eu.zuinnote.example.springwebdemo.order.Order;
import eu.zuinnote.example.springwebdemo.order.OrderRepository;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class OrderController {
    private OrderRepository orders;

    public OrderController(OrderRepository orders) {
        this.orders = orders;
    }

    @GetMapping("/order/{id}")
    public Order getOrder(@PathVariable UUID id) {
        this.log.debug(String.format("Returning order for order id: %s", id));
        return orders.findById(id);
    }

    @GetMapping("/order")
    public Page<Order> getAllOrders(Pageable pageable) {
        this.log.debug("Returning all orders");
        return orders.findAll(pageable);
    }
}
