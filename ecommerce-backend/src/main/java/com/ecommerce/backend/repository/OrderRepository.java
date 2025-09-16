package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumber(@Param("orderNumber") String orderNumber);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o WHERE o.orderNumber = :orderNumber")
    boolean existsByOrderNumber(@Param("orderNumber") String orderNumber);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC")
    Page<Order> findByUserIdOrderByOrderDateDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.id = :orderId")
    Optional<Order> findByIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberAndUserId(@Param("orderNumber") String orderNumber, @Param("userId") Long userId);

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status ORDER BY o.orderDate DESC")
    Page<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    Page<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    Page<Order> findByUserIdAndOrderDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.totalAmount >= :minAmount AND o.totalAmount <= :maxAmount ORDER BY o.orderDate DESC")
    Page<Order> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.trackingNumber = :trackingNumber")
    Optional<Order> findByTrackingNumber(@Param("trackingNumber") String trackingNumber);

    // Comprehensive search for orders
    @Query("SELECT o FROM Order o WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "o.orderNumber LIKE CONCAT('%', :search, '%') OR " +
           "LOWER(o.shippingCity) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.shippingState) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "o.trackingNumber LIKE CONCAT('%', :search, '%')) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderDate <= :endDate) " +
           "ORDER BY o.orderDate DESC")
    Page<Order> searchOrders(@Param("search") String search,
                            @Param("status") OrderStatus status,
                            @Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate,
                            Pageable pageable);

    // User-specific search (security constraint)
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND " +
           "(:search IS NULL OR :search = '' OR " +
           "o.orderNumber LIKE CONCAT('%', :search, '%') OR " +
           "o.trackingNumber LIKE CONCAT('%', :search, '%')) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderDate <= :endDate) " +
           "ORDER BY o.orderDate DESC")
    Page<Order> searchUserOrders(@Param("userId") Long userId,
                                @Param("search") String search,
                                @Param("status") OrderStatus status,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate,
                                Pageable pageable);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :orderId")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status);

    @Modifying
    @Query("UPDATE Order o SET o.trackingNumber = :trackingNumber, o.status = :status, o.shippedDate = :shippedDate WHERE o.id = :orderId")
    int updateShippingInfo(@Param("orderId") Long orderId, @Param("trackingNumber") String trackingNumber, @Param("status") OrderStatus status, @Param("shippedDate") LocalDateTime shippedDate);

    // Analytics and reporting queries
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :since")
    long countOrdersSince(@Param("since") LocalDateTime since);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalAmountByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.status = :status")
    BigDecimal avgTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status ORDER BY COUNT(o) DESC")
    List<Object[]> getOrderCountByStatus();

    @Query("SELECT DATE(o.orderDate), COUNT(o) FROM Order o WHERE o.orderDate >= :since GROUP BY DATE(o.orderDate) ORDER BY DATE(o.orderDate)")
    List<Object[]> getDailyOrderCount(@Param("since") LocalDateTime since);

    @Query("SELECT DATE(o.orderDate), SUM(o.totalAmount) FROM Order o WHERE o.orderDate >= :since GROUP BY DATE(o.orderDate) ORDER BY DATE(o.orderDate)")
    List<Object[]> getDailyRevenue(@Param("since") LocalDateTime since);

    // Top customers by order count or total spent
    @Query("SELECT o.user.id, o.user.username, COUNT(o) FROM Order o GROUP BY o.user.id, o.user.username ORDER BY COUNT(o) DESC")
    List<Object[]> getTopCustomersByOrderCount(Pageable pageable);

    @Query("SELECT o.user.id, o.user.username, SUM(o.totalAmount) FROM Order o GROUP BY o.user.id, o.user.username ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> getTopCustomersByTotalSpent(Pageable pageable);

    // Recent orders for dashboard
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    Page<Order> findRecentOrders(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.orderDate DESC")
    Page<Order> findByStatusIn(@Param("statuses") List<OrderStatus> statuses, Pageable pageable);

    // Fetch orders with items for performance
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.user.id = :userId ORDER BY o.orderDate DESC")
    List<Order> findByUserIdWithItems(@Param("userId") Long userId);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithItems(@Param("orderNumber") String orderNumber);
}