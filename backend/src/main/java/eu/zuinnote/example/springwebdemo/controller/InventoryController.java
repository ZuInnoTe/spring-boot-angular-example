package eu.zuinnote.example.springwebdemo.controller;

import eu.zuinnote.example.springwebdemo.inventory.Product;
import eu.zuinnote.example.springwebdemo.inventory.ProductRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {
    private ProductRepository products;

    public InventoryController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable UUID id) {
        return products.findById(id);
    }

    @GetMapping("/product")
    public Page<Product> getAllProducts(Pageable pageable) {
        return products.findAll(pageable);
    }
}
