package eu.zuinnote.example.springwebdemo.inventory;

import eu.zuinnote.example.springwebdemo.utility.SanitizerService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product")
public class Product {

    @Id private UUID id;

    @Column(name = "name", length = 50, nullable = false, unique = false)
    private String name;

    private BigDecimal price;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Product sanitize() {
        this.setName(SanitizerService.NO_HTML.sanitize(this.getName()));
        return this;
    }
}
