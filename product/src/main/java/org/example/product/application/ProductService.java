package org.example.product.application;

import jakarta.transaction.Transactional;
import java.util.List;
import org.example.product.application.dto.ProductReserveCommand;
import org.example.product.application.dto.ProductReserveResult;
import org.example.product.domain.Product;
import org.example.product.domain.ProductReservation;
import org.example.product.infrastructure.ProductRepository;
import org.example.product.infrastructure.ProductReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductReservationRepository productReservationRepository;

    public ProductService(ProductRepository productRepository, ProductReservationRepository productReservationRepository) {
        this.productRepository = productRepository;
        this.productReservationRepository = productReservationRepository;
    }

    @Transactional
    public ProductReserveResult tryReserve(ProductReserveCommand command) {
        List<ProductReservation> exists = productReservationRepository.findAllByRequestId(command.requestId());

        if(!exists.isEmpty()) {
            long totalPrice = exists.stream().mapToLong(ProductReservation::getReservedPrice).sum();

            return new ProductReserveResult(totalPrice);
        }

        Long totalPrice = 0L;
        for(ProductReserveCommand.ReserveItem item : command.items()) {
            Product product = productRepository.findById(item.productId()).orElseThrow();
            Long price = product.reserve(item.reserveQuantity());
            totalPrice += price;

            productRepository.save(product);
            productReservationRepository.save(
                new ProductReservation(
                    command.requestId(),
                    item.productId(),
                    item.reserveQuantity(),
                    price
                )
            );

        }

        return new ProductReserveResult(totalPrice);
    }
}
