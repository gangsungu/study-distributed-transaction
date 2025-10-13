package org.example.product.application;

import jakarta.transaction.Transactional;
import java.util.List;
import org.example.product.application.dto.ProductReserveCancelCommand;
import org.example.product.application.dto.ProductReserveCommand;
import org.example.product.application.dto.ProductReserveConfirmCommand;
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

    @Transactional
    public void confirmReserve(ProductReserveConfirmCommand command) {
        List<ProductReservation> reservations = productReservationRepository.findAllByRequestId(command.requestId());

        if(reservations.isEmpty()) {
            throw new RuntimeException("예약된 정보가 없습니다.");
        }

        boolean alreadyConfirmed = reservations.stream()
            .anyMatch(item -> item.getStatus() == ProductReservation.ProductReservationStatus.CONFIRMED);

        if(alreadyConfirmed) {
            System.out.println("이미 확정된 예약입니다.");
            return;
        }

        for(ProductReservation reservation : reservations) {
            Product product = productRepository.findById(reservation.getProductId()).orElseThrow();

            product.confirm(reservation.getReservedQuantity());
            reservation.confirm();

            productRepository.save(product);
            productReservationRepository.save(reservation);
        }
    }

    @Transactional
    public void cancelReserve(ProductReserveCancelCommand command) {
        List<ProductReservation> reservations = productReservationRepository.findAllByRequestId(command.requestId());

        if(reservations.isEmpty()) {
            throw new RuntimeException("예약된 정보가 존재하지 않습니다.");
        }

        boolean alreadyCancelled = reservations.stream()
            .anyMatch(item -> item.getStatus() == ProductReservation.ProductReservationStatus.CANCELLED);

        if(alreadyCancelled) {
            System.out.println("이미 취소된 요청입니다.");
            return;
        }

        for(ProductReservation reservation : reservations) {
            Product product = productRepository.findById(reservation.getProductId()).orElseThrow();

            product.cancel(reservation.getReservedQuantity());
            reservation.cancel();

            productRepository.save(product);
            productReservationRepository.save(reservation);
        }
    }
}
