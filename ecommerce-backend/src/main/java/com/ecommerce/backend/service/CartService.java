package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.CartItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.CustomExceptions.*;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Get or create cart for user
     */
    public Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .totalAmount(BigDecimal.ZERO)
                            .totalItems(0)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Get cart by user ID with items
     */
    @Transactional(readOnly = true)
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
    }

    /**
     * Add item to cart
     */
    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        // Check if product is active and has sufficient stock
        if (!product.isActive()) {
            throw new ProductNotAvailableException("Product is not available");
        }

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        
        if (existingItem.isPresent()) {
            // Update existing item quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException("Insufficient stock. Available: " + product.getStockQuantity());
            }
            
            item.updateQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Add new item to cart
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .build();
            
            cart.addCartItem(newItem);
            cartItemRepository.save(newItem);
        }

        cart.updateTotals();
        return cartRepository.save(cart);
    }

    /**
     * Update cart item quantity
     */
    public Cart updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Cart cart = getCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + cartItemId));

        // Verify cart item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("Cart item does not belong to user's cart");
        }

        // Check stock availability
        if (cartItem.getProduct().getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock. Available: " + cartItem.getProduct().getStockQuantity());
        }

        cartItem.updateQuantity(quantity);
        cartItemRepository.save(cartItem);

        cart.updateTotals();
        return cartRepository.save(cart);
    }

    /**
     * Remove item from cart
     */
    public Cart removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + cartItemId));

        // Verify cart item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("Cart item does not belong to user's cart");
        }

        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);

        cart.updateTotals();
        return cartRepository.save(cart);
    }

    /**
     * Clear all items from cart
     */
    public Cart clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        
        if (cart.isEmpty()) {
            throw new CartEmptyException("Cart is already empty");
        }

        cartItemRepository.deleteByCartId(cart.getId());
        cart.clearCart();
        cart.updateTotals();

        return cartRepository.save(cart);
    }

    /**
     * Get cart item count for user
     */
    @Transactional(readOnly = true)
    public Integer getCartItemCount(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(Cart::getTotalItems)
                .orElse(0);
    }

    /**
     * Check if cart exists for user
     */
    @Transactional(readOnly = true)
    public boolean cartExists(Long userId) {
        return cartRepository.existsByUserId(userId);
    }

    /**
     * Get cart total amount
     */
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(Cart::getTotalAmount)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Merge guest cart with user cart (for future use)
     */
    public Cart mergeGuestCart(Long userId, Cart guestCart) {
        if (guestCart == null || guestCart.isEmpty()) {
            return getOrCreateCart(userId);
        }

        Cart userCart = getOrCreateCart(userId);

        for (CartItem guestItem : guestCart.getCartItems()) {
            addItemToCart(userId, guestItem.getProduct().getId(), guestItem.getQuantity());
        }

        return getCartByUserId(userId);
    }

    /**
     * Validate cart for checkout
     */
    @Transactional(readOnly = true)
    public void validateCartForCheckout(Long userId) {
        Cart cart = getCartByUserId(userId);
        
        if (cart.isEmpty()) {
            throw new CartEmptyException("Cannot checkout with empty cart");
        }

        // Check stock availability for all items
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (!product.isActive()) {
                throw new ProductNotAvailableException("Product '" + product.getName() + "' is no longer available");
            }
            
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for product '" + product.getName() + 
                    "'. Available: " + product.getStockQuantity() + 
                    ", Required: " + item.getQuantity()
                );
            }
        }
    }
}