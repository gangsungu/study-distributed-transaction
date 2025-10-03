package org.example.monolithic.order.infrastructure;

import java.util.List;
import org.example.monolithic.order.domain.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

    List<OrderItem> findAllByOrderId(Long orderId);
}
