package com.badminton.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
    // 這個方法會自動被翻譯成：SELECT * FROM OrderItems WHERE order_id = ?
    // 讓我們可以很方便地找出某張訂單底下的所有明細
    List<OrderItem> findByOrderId(Integer orderId);
}
