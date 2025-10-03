package org.example.monolithic.order.infrastructure;

import org.example.monolithic.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
