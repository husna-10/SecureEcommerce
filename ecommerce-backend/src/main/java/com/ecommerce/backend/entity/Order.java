package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_user", columnList = "user_id"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_number", columnList = "order_number"),
    @Index(name = "idx_order_date", columnList = "order_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // One-to-Many relationship with OrderItems
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "order_date", nullable = false)
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    @Column(name = "total_items", nullable = false)
    @NotNull(message = "Total items is required")
    private Integer totalItems;

    // Shipping Address
    @Column(name = "shipping_address_line1", nullable = false, length = 255)
    @NotNull(message = "Shipping address line 1 is required")
    @Size(max = 255, message = "Shipping address line 1 cannot exceed 255 characters")
    private String shippingAddressLine1;

    @Column(name = "shipping_address_line2", length = 255)
    @Size(max = 255, message = "Shipping address line 2 cannot exceed 255 characters")
    private String shippingAddressLine2;

    @Column(name = "shipping_city", nullable = false, length = 100)
    @NotNull(message = "Shipping city is required")
    @Size(max = 100, message = "Shipping city cannot exceed 100 characters")
    private String shippingCity;

    @Column(name = "shipping_state", nullable = false, length = 100)
    @NotNull(message = "Shipping state is required")
    @Size(max = 100, message = "Shipping state cannot exceed 100 characters")
    private String shippingState;

    @Column(name = "shipping_postal_code", nullable = false, length = 20)
    @NotNull(message = "Shipping postal code is required")
    @Size(max = 20, message = "Shipping postal code cannot exceed 20 characters")
    private String shippingPostalCode;

    @Column(name = "shipping_country", nullable = false, length = 100)
    @NotNull(message = "Shipping country is required")
    @Size(max = 100, message = "Shipping country cannot exceed 100 characters")
    private String shippingCountry;

    // Payment Information
    @Column(name = "payment_method", length = 50)
    @Size(max = 50, message = "Payment method cannot exceed 50 characters")
    private String paymentMethod;

    @Column(name = "payment_status", length = 50)
    @Size(max = 50, message = "Payment status cannot exceed 50 characters")
    private String paymentStatus;

    // Tracking Information
    @Column(name = "tracking_number", length = 100)
    @Size(max = 100, message = "Tracking number cannot exceed 100 characters")
    private String trackingNumber;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @PrePersist
    private void generateOrderNumber() {
        if (orderNumber == null) {
            this.orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        updateTotals();
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
        updateTotals();
    }

    public void updateTotals() {
        this.totalItems = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
        
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getFullShippingAddress() {
        StringBuilder address = new StringBuilder();
        address.append(shippingAddressLine1);
        if (shippingAddressLine2 != null && !shippingAddressLine2.trim().isEmpty()) {
            address.append(", ").append(shippingAddressLine2);
        }
        address.append(", ").append(shippingCity);
        address.append(", ").append(shippingState);
        address.append(" ").append(shippingPostalCode);
        address.append(", ").append(shippingCountry);
        return address.toString();
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.PROCESSING;
    }

    public void markAsShipped(String trackingNumber) {
        this.status = OrderStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        this.shippedDate = LocalDateTime.now();
    }

    public void markAsDelivered() {
        this.status = OrderStatus.DELIVERED;
        this.deliveredDate = LocalDateTime.now();
    }

    public void cancel() {
        if (canBeCancelled()) {
            this.status = OrderStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Order cannot be cancelled in current status: " + status);
        }
    }
}