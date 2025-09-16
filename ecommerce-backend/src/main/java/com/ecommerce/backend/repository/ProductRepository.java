package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.sku = :sku")
    Optional<Product> findBySku(@Param("sku") String sku);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.sku = :sku")
    boolean existsBySku(@Param("sku") String sku);

    @Query("SELECT p FROM Product p WHERE p.active = true")
    Page<Product> findActiveProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = :active")
    Page<Product> findByActive(@Param("active") Boolean active, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.active = true")
    Page<Product> findActiveByCategoryIgnoreCase(@Param("category") String category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.active = true")
    Page<Product> findActiveByBrandIgnoreCase(@Param("brand") String brand, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.active = true")
    Page<Product> findInStockProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0 AND p.active = true")
    Page<Product> findOutOfStockProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold AND p.active = true")
    Page<Product> findLowStockProducts(@Param("threshold") Integer threshold, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.active = true")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    // Advanced search with multiple filters
    @Query("SELECT p FROM Product p WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR LOWER(p.category) = LOWER(:category)) AND " +
           "(:brand IS NULL OR :brand = '' OR LOWER(p.brand) = LOWER(:brand)) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR (:inStock = false)) AND " +
           "p.active = true")
    Page<Product> searchProducts(@Param("search") String search,
                                @Param("category") String category,
                                @Param("brand") String brand,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                @Param("inStock") Boolean inStock,
                                Pageable pageable);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true ORDER BY p.category")
    List<String> findAllCategories();

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.active = true AND p.brand IS NOT NULL ORDER BY p.brand")
    List<String> findAllBrands();

    @Query("SELECT p FROM Product p WHERE p.name ILIKE :name AND p.active = true")
    Page<Product> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :productId AND p.stockQuantity >= :quantity")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :productId")
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Product p SET p.active = :active WHERE p.id = :productId")
    int updateActiveStatus(@Param("productId") Long productId, @Param("active") Boolean active);

    // Statistical queries for admin dashboard
    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true")
    long countActiveProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = 0 AND p.active = true")
    long countOutOfStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= :threshold AND p.active = true")
    long countLowStockProducts(@Param("threshold") Integer threshold);

    @Query("SELECT p.category, COUNT(p) FROM Product p WHERE p.active = true GROUP BY p.category ORDER BY COUNT(p) DESC")
    List<Object[]> getProductCountByCategory();

    @Query("SELECT AVG(p.price) FROM Product p WHERE p.active = true")
    BigDecimal getAveragePrice();

    @Query("SELECT MIN(p.price) FROM Product p WHERE p.active = true")
    BigDecimal getMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p WHERE p.active = true")
    BigDecimal getMaxPrice();

    // Featured/Popular products (can be enhanced with sales data)
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.createdAt DESC")
    Page<Product> findNewestProducts(Pageable pageable);

    // For admin - include inactive products
    @Query("SELECT p FROM Product p WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:active IS NULL OR p.active = :active)")
    Page<Product> adminSearchProducts(@Param("search") String search, @Param("active") Boolean active, Pageable pageable);
    
    // Additional methods required by ProductService
    Page<Product> findByActiveTrue(Pageable pageable);
    
    Optional<Product> findByIdAndActiveTrue(Long id);
    
    boolean existsByIdAndActiveTrue(Long id);
    
    long countByActiveTrue();
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndActiveTrue(
            @Param("query") String name, @Param("query") String description, Pageable pageable);
    
    Page<Product> findByCategoryIgnoreCaseAndActiveTrue(String category, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND " +
           "LOWER(p.category) = LOWER(:category)")
    Page<Product> findByNameContainingIgnoreCaseAndCategoryIgnoreCaseAndActiveTrue(
            @Param("name") String name, @Param("category") String category, Pageable pageable);
    
    Page<Product> findByStockQuantityLessThanAndActiveTrue(int threshold, Pageable pageable);
}