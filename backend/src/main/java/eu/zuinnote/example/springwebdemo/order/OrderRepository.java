package eu.zuinnote.example.springwebdemo.order;

import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, UUID> {
    Order findById(UUID id);
}
