package org.example.product.infrastructure;

import java.util.List;
import org.example.product.domain.ProductReservation;
import org.springframework.data.repository.CrudRepository;

public interface ProductReservationRepository extends CrudRepository<ProductReservation, Long> {
    List<ProductReservation> findAllByRequestId(String reqeustId);
}
