package com.badminton.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    // Spring Data JPA 的魔法：只要按照命名規則宣告方法名稱
    // 它就會自動幫你產出類似：SELECT * FROM Orders WHERE member_id = ? 的 SQL 語法
    List<Order> findByMemberId(Integer memberId);
}
