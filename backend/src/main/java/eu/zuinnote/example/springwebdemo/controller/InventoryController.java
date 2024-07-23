package eu.zuinnote.example.springwebdemo.controller;

import eu.zuinnote.example.springwebdemo.inventory.Product;
import eu.zuinnote.example.springwebdemo.inventory.ProductRepository;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class InventoryController {
    private ProductRepository products;

    public InventoryController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable UUID id) {
        this.log.debug(String.format("Returning product for product id: %s", id));
        // sanitze for malicious HTML/scripts
        return products.findById(id).sanitize();
    }

    @GetMapping("/product")
    public Page<Product> getAllProducts(Pageable pageable) {
        this.log.debug("Returning all products");
        // sanitze for malicious HTML/scripts
        return products.findAll(pageable).map(product -> product.sanitize());
    }
}
