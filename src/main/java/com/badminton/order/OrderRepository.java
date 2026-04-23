package com.badminton.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    // Spring Data JPA 的魔法：member 現在是 @ManyToOne 關聯物件
    // 使用底線 _ 表示「進入 member 物件裡面找 memberId 欄位」
    // 等同於：SELECT * FROM Orders WHERE member_id = ?
    List<Order> findByMember_MemberId(Integer memberId);
}
