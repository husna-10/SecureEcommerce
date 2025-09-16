package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_order", columnList = "order_id"),
    @Index(name = "idx_order_item_product", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    // Many-to-One relationship with Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Many-to-One relationship with Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false, length = 200)
    @NotNull(message = "Product name is required")
    @Size(max = 200, message = "Product name cannot exceed 200 characters")
    private String productName;

    @Column(name = "product_sku", length = 100)
    @Size(max = 100, message = "Product SKU cannot exceed 100 characters")
    private String productSku;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    @Column(name = "sub_total", nullable = false, precision = 19, scale = 2)
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

    // Copy product details to preserve order history
    public void copyProductDetails() {
        if (product != null) {
            this.productName = product.getName();
            this.productSku = product.getSku();
            this.unitPrice = product.getPrice();
        }
    }
}