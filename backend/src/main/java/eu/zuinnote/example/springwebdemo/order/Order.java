package eu.zuinnote.example.springwebdemo.order;

import eu.zuinnote.example.springwebdemo.inventory.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "order")
public class Order {

    @Id private UUID id;

    private ZonedDateTime orderDateTime;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ZonedDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(ZonedDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order sanitize() {
        this.setProduct(this.getProduct().sanitize());
        return this;
    }
}
