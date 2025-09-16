package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ApiResponse;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Products", description = "Product management APIs")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products", description = "Retrieve a paginated list of products")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Search query") @RequestParam(required = false) String search,
            @Parameter(description = "Category filter") @RequestParam(required = false) String category,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Fetching products - page: {}, size: {}, search: {}, category: {}", page, size, search, category);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Product> products = productService.getAllProducts(search, category, pageable);
        
        ApiResponse<Page<Product>> response = ApiResponse.<Page<Product>>builder()
                .success(true)
                .message("Products retrieved successfully")
                .data(products)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product by ID", description = "Retrieve a single product by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        
        log.info("Fetching product with id: {}", id);
        
        Product product = productService.getProductById(id);
        
        ApiResponse<Product> response = ApiResponse.<Product>builder()
                .success(true)
                .message("Product retrieved successfully")
                .data(product)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create new product", description = "Create a new product (Admin only)", 
               security = @SecurityRequirement(name = "bearer-key"))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody Product product) {
        log.info("Creating new product: {}", product.getName());
        
        Product createdProduct = productService.createProduct(product);
        
        ApiResponse<Product> response = ApiResponse.<Product>builder()
                .success(true)
                .message("Product created successfully")
                .data(createdProduct)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update product", description = "Update an existing product (Admin only)",
               security = @SecurityRequirement(name = "bearer-key"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody Product product) {
        
        log.info("Updating product with id: {}", id);
        
        Product updatedProduct = productService.updateProduct(id, product);
        
        ApiResponse<Product> response = ApiResponse.<Product>builder()
                .success(true)
                .message("Product updated successfully")
                .data(updatedProduct)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete product", description = "Delete a product (Admin only)",
               security = @SecurityRequirement(name = "bearer-key"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        
        log.info("Deleting product with id: {}", id);
        
        productService.deleteProduct(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Product deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get products by category", description = "Retrieve products filtered by category")
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<Product>>> getProductsByCategory(
            @Parameter(description = "Category name") @PathVariable String category,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching products by category: {}", category);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByCategory(category, pageable);
        
        ApiResponse<Page<Product>> response = ApiResponse.<Page<Product>>builder()
                .success(true)
                .message("Products retrieved successfully")
                .data(products)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search products", description = "Search products by name or description")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Product>>> searchProducts(
            @Parameter(description = "Search query") @RequestParam String query,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Searching products with query: {}", query);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.searchProducts(query, pageable);
        
        ApiResponse<Page<Product>> response = ApiResponse.<Page<Product>>builder()
                .success(true)
                .message("Search results retrieved successfully")
                .data(products)
                .build();
        
        return ResponseEntity.ok(response);
    }
}