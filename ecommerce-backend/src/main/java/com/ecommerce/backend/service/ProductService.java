package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getAllProducts(String search, String category, Pageable pageable) {
        if (search != null && !search.trim().isEmpty() && category != null && !category.trim().isEmpty()) {
            return productRepository.findByNameContainingIgnoreCaseAndCategoryIgnoreCaseAndActiveTrue(
                    search.trim(), category.trim(), pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndActiveTrue(
                    search.trim(), search.trim(), pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            return productRepository.findByCategoryIgnoreCaseAndActiveTrue(category.trim(), pageable);
        } else {
            return productRepository.findByActiveTrue(pageable);
        }
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        // Set audit fields
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setActive(true);
        
        // Generate SKU if not provided
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            product.setSku(generateSKU(product.getName()));
        }
        
        log.info("Creating product: {}", product.getName());
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productUpdate) {
        Product existingProduct = getProductById(id);
        
        // Update fields
        existingProduct.setName(productUpdate.getName());
        existingProduct.setDescription(productUpdate.getDescription());
        existingProduct.setPrice(productUpdate.getPrice());
        existingProduct.setCategory(productUpdate.getCategory());
        existingProduct.setStockQuantity(productUpdate.getStockQuantity());
        existingProduct.setBrand(productUpdate.getBrand());
        existingProduct.setImageUrl(productUpdate.getImageUrl());
        existingProduct.setTags(productUpdate.getTags());
        existingProduct.setWeight(productUpdate.getWeight());
        existingProduct.setDimensions(productUpdate.getDimensions());
        existingProduct.setActive(productUpdate.isActive());
        existingProduct.setUpdatedAt(LocalDateTime.now());
        
        if (productUpdate.getSku() != null && !productUpdate.getSku().trim().isEmpty()) {
            existingProduct.setSku(productUpdate.getSku());
        }
        
        log.info("Updating product: {}", existingProduct.getName());
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        
        // Soft delete by setting active to false
        product.setActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        
        productRepository.save(product);
        log.info("Product deleted (soft): {}", product.getName());
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryIgnoreCaseAndActiveTrue(category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String query, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndActiveTrue(
                query, query, pageable);
    }

    private String generateSKU(String productName) {
        // Simple SKU generation: First 3 letters + timestamp
        String prefix = productName.replaceAll("[^a-zA-Z]", "").toUpperCase();
        if (prefix.length() > 3) {
            prefix = prefix.substring(0, 3);
        } else if (prefix.length() < 3) {
            prefix = String.format("%-3s", prefix).replace(' ', 'X');
        }
        
        long timestamp = System.currentTimeMillis() % 100000; // Last 5 digits
        return prefix + "-" + timestamp;
    }

    // Additional utility methods
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return productRepository.existsByIdAndActiveTrue(id);
    }

    @Transactional(readOnly = true)
    public long countActiveProducts() {
        return productRepository.countByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Page<Product> getLowStockProducts(int threshold, Pageable pageable) {
        return productRepository.findByStockQuantityLessThanAndActiveTrue(threshold, pageable);
    }
}