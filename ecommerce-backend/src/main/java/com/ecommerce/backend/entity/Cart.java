package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts", indexes = {
    @Index(name = "idx_cart_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart extends BaseEntity {

    // One-to-One relationship with User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // One-to-Many relationship with CartItems
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "total_items")
    @Builder.Default
    private Integer totalItems = 0;

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
        updateTotals();
    }

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
        updateTotals();
    }

    public void clearCart() {
        cartItems.clear();
        updateTotals();
    }

    public void updateTotals() {
        this.totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        
        this.totalAmount = cartItems.stream()
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public int getTotalItemCount() {
        return cartItems.size();
    }

    public BigDecimal getTotalAmount() {
        if (totalAmount == null) {
            updateTotals();
        }
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }
}