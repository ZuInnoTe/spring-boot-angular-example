package eu.zuinnote.example.springwebdemo.inventory.internal;

import eu.zuinnote.example.springwebdemo.inventory.Product;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, UUID> {

    Product findById(UUID id);
}
