package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items", 
       indexes = {
           @Index(name = "idx_cart_item_cart", columnList = "cart_id"),
           @Index(name = "idx_cart_item_product", columnList = "product_id")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    // Many-to-One relationship with Cart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Many-to-One relationship with Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    @Column(name = "sub_total", precision = 19, scale = 2)
    private BigDecimal subTotal;

    @PrePersist
    @PreUpdate
    private void calculateSubTotal() {
        if (unitPrice != null && quantity != null) {
            this.subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getSubTotal() {
        if (subTotal == null) {
            calculateSubTotal();
        }
        return subTotal != null ? subTotal : BigDecimal.ZERO;
    }

    public void updateQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
        calculateSubTotal();
    }

    public void incrementQuantity() {
        this.quantity++;
        calculateSubTotal();
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            calculateSubTotal();
        }
    }
}