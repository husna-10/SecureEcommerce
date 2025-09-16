package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ApiResponse;
import com.ecommerce.backend.dto.CartDto;
import com.ecommerce.backend.dto.CartItemDto;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.service.CartService;
import com.ecommerce.backend.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cart Management", description = "APIs for managing shopping cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user's cart", description = "Retrieve the current user's shopping cart with items")
    public ResponseEntity<ApiResponse<CartDto>> getCart(Authentication authentication) {
        log.info("Getting cart for user");
        
        Long userId = UserUtil.getCurrentUserId(authentication);
        Cart cart = cartService.getOrCreateCart(userId);
        CartDto cartDto = convertToCartDto(cart);

        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cartDto));
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add item to cart", description = "Add a product to the user's shopping cart")
    public ResponseEntity<ApiResponse<CartDto>> addItemToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {
        
        log.info("Adding item to cart - Product ID: {}, Quantity: {}", request.productId, request.quantity);
        
        Long userId = UserUtil.getCurrentUserId(authentication);
        Cart cart = cartService.addItemToCart(userId, request.productId, request.quantity);
        CartDto cartDto = convertToCartDto(cart);

        return ResponseEntity.ok(ApiResponse.success("Item added to cart successfully", cartDto));
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update cart item quantity", description = "Update the quantity of an item in the cart")
    public ResponseEntity<ApiResponse<CartDto>> updateCartItemQuantity(
            @Parameter(description = "Cart item ID") @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication) {
        
        log.info("Updating cart item {} quantity to {}", itemId, request.quantity);
        
        Long userId = UserUtil.getCurrentUserId(authentication);
        Cart cart = cartService.updateCartItemQuantity(userId, itemId, request.quantity);
        CartDto cartDto = convertToCartDto(cart);

        return ResponseEntity.ok(ApiResponse.success("Cart item updated successfully", cartDto));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Remove item from cart", description = "Remove an item from the user's shopping cart")
    public ResponseEntity<ApiResponse<CartDto>> removeItemFromCart(
            @Parameter(description = "Cart item ID") @PathVariable Long itemId,
            Authentication authentication) {
        
        log.info("Removing cart item {}", itemId);
        
        Long userId = UserUtil.getCurrentUserId(authentication);
        Cart cart = cartService.removeItemFromCart(userId, itemId);
        CartDto cartDto = convertToCartDto(cart);

        return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully", cartDto));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Clear cart", description = "Remove all items from the user's shopping cart")
    public ResponseEntity<ApiResponse<String>> clearCart(Authentication authentication) {
        log.info("Clearing cart for user");
        
        Long userId = UserUtil.getCurrentUserId(authentication);
        cartService.clearCart(userId);

        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", "Cart is now empty"));
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get cart item count", description = "Get the total number of items in the user's cart")
    public ResponseEntity<ApiResponse<Integer>> getCartItemCount(Authentication authentication) {
        Long userId = UserUtil.getCurrentUserId(authentication);
        Integer count = cartService.getCartItemCount(userId);

        return ResponseEntity.ok(ApiResponse.success("Cart item count retrieved successfully", count));
    }

    @GetMapping("/total")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get cart total", description = "Get the total amount of the user's cart")
    public ResponseEntity<ApiResponse<BigDecimal>> getCartTotal(Authentication authentication) {
        Long userId = UserUtil.getCurrentUserId(authentication);
        BigDecimal total = cartService.getCartTotal(userId);

        return ResponseEntity.ok(ApiResponse.success("Cart total retrieved successfully", total));
    }

    @PostMapping("/validate")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Validate cart for checkout", description = "Validate that all items in cart are available and in stock")
    public ResponseEntity<ApiResponse<String>> validateCart(Authentication authentication) {
        Long userId = UserUtil.getCurrentUserId(authentication);
        cartService.validateCartForCheckout(userId);

        return ResponseEntity.ok(ApiResponse.success("Cart is valid for checkout", "All items are available"));
    }

    // Helper method to convert Cart entity to CartDto
    private CartDto convertToCartDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        List<CartItemDto> items = cart.getCartItems().stream()
                .map(this::convertToCartItemDto)
                .collect(Collectors.toList());

        return CartDto.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(cart.getTotalItems())
                .totalAmount(cart.getTotalAmount())
                .build();
    }

    private CartItemDto convertToCartItemDto(com.ecommerce.backend.entity.CartItem item) {
        return CartItemDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productSku(item.getProduct().getSku())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subTotal(item.getSubTotal())
                .build();
    }

    // Request DTOs
    public static class AddToCartRequest {
        @NotNull(message = "Product ID is required")
        public Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        public Integer quantity;
    }

    public static class UpdateCartItemRequest {
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        public Integer quantity;
    }
}