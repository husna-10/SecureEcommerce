package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = :userId")
    List<OrderItem> findByUserId(@Param("userId") Long userId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.product.id = :productId")
    List<OrderItem> findByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.id = :orderId")
    long countByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long sumQuantityByProductId(@Param("productId") Long productId);

    @Query("SELECT SUM(oi.subTotal) FROM OrderItem oi WHERE oi.order.id = :orderId")
    BigDecimal sumTotalByOrderId(@Param("orderId") Long orderId);

    // Analytics queries for product sales
    @Query("SELECT oi.product.id, oi.productName, SUM(oi.quantity), SUM(oi.subTotal) " +
           "FROM OrderItem oi " +
           "WHERE oi.order.orderDate >= :since " +
           "GROUP BY oi.product.id, oi.productName " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getTopSellingProducts(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT oi.product.id, oi.productName, SUM(oi.subTotal) " +
           "FROM OrderItem oi " +
           "WHERE oi.order.orderDate >= :since " +
           "GROUP BY oi.product.id, oi.productName " +
           "ORDER BY SUM(oi.subTotal) DESC")
    List<Object[]> getTopRevenueProducts(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderIdWithProduct(@Param("orderId") Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = :userId")
    Page<OrderItem> findByUserIdPaginated(@Param("userId") Long userId, Pageable pageable);

    // Product performance analytics
    @Query("SELECT DATE(oi.order.orderDate), SUM(oi.quantity) " +
           "FROM OrderItem oi " +
           "WHERE oi.product.id = :productId AND oi.order.orderDate >= :since " +
           "GROUP BY DATE(oi.order.orderDate) " +
           "ORDER BY DATE(oi.order.orderDate)")
    List<Object[]> getDailySalesByProduct(@Param("productId") Long productId, @Param("since") LocalDateTime since);

    @Query("SELECT oi.order.shippingState, SUM(oi.quantity) " +
           "FROM OrderItem oi " +
           "WHERE oi.product.id = :productId " +
           "GROUP BY oi.order.shippingState " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getSalesByRegionForProduct(@Param("productId") Long productId);

    // Recently purchased products by user for recommendations
    @Query("SELECT DISTINCT oi.product.id " +
           "FROM OrderItem oi " +
           "WHERE oi.order.user.id = :userId " +
           "ORDER BY oi.order.orderDate DESC")
    List<Long> getRecentlyPurchasedProductIds(@Param("userId") Long userId, Pageable pageable);
}