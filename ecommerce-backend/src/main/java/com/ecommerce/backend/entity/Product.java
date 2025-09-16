package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_name", columnList = "name"),
    @Index(name = "idx_product_category", columnList = "category"),
    @Index(name = "idx_product_active", columnList = "active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity = 0;

    @Size(max = 100, message = "SKU must not exceed 100 characters")
    @Column(name = "sku", length = 100, unique = true)
    private String sku;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    @Column(name = "brand", length = 100)
    private String brand;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Size(max = 255, message = "Tags must not exceed 255 characters")
    @Column(name = "tags", length = 255)
    private String tags;

    @DecimalMin(value = "0.0", message = "Weight cannot be negative")
    @Digits(integer = 5, fraction = 3, message = "Weight must have at most 3 decimal places")
    @Column(name = "weight", precision = 8, scale = 3)
    private BigDecimal weight;

    @Size(max = 100, message = "Dimensions must not exceed 100 characters")
    @Column(name = "dimensions", length = 100)
    private String dimensions;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}