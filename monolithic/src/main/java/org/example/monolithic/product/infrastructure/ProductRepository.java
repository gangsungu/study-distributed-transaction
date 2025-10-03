package org.example.monolithic.product.infrastructure;

import org.example.monolithic.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}