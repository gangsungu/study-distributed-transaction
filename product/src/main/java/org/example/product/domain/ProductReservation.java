package org.example.product.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_reservation")
public class ProductReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;

    private Long productId;

    private Long reservationQuantity;

    private Long reservedPrice;

    @Enumerated(EnumType.STRING)
    private ProductReservationStatus status;

    public ProductReservation() {

    }

    public ProductReservation(String requestId, Long productId, Long reservationQuantity, Long reservedPrice) {
        this.requestId = requestId;
        this.productId = productId;
        this.reservationQuantity = reservationQuantity;
        this.reservedPrice = reservedPrice;
        status = ProductReservationStatus.RESERVED;
    }

    public Long getReservedPrice() {
        return reservedPrice;
    }

    public enum ProductReservationStatus {
        RESERVED,
        CONFIRMED,
        CANCELLED,
    }
}
