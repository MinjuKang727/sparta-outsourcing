package com.sparta.spartaoutsourcing.order.repository;

import com.sparta.spartaoutsourcing.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);


//    일간 고객 쿼리
    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o WHERE o.store.id = :storeId AND DATE(o.createdAt) = CURRENT_DATE")
    Long countDailyCustomers(@Param("storeId") Long storeId);

//    월간 고객 쿼리
    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o WHERE o.store.id = :storeId AND YEAR(o.createdAt) = YEAR(CURRENT_DATE) AND MONTH(o.createdAt) = MONTH(CURRENT_DATE)")
    Long countMonthlyCustomers(@Param("storeId") Long storeId);

//    일간 매출 쿼리
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.store.id = :storeId AND DATE(o.createdAt) = CURRENT_DATE")
    Long sumDailySales(@Param("storeId") Long storeId);

//    월간 매출 쿼리
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.store.id = :storeId AND YEAR(o.createdAt) = YEAR(CURRENT_DATE) AND MONTH(o.createdAt) = MONTH(CURRENT_DATE)")
    Long sumMonthlySales(@Param("storeId") Long storeId);

}
