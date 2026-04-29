package com.badminton.order;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.badminton.member.Member;

@Entity
@Table(name = "Orders") // 若 SQL Server 已經存在此表，通常叫做 Orders
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    // @ManyToOne: 多筆訂單對應同一位會員，JPA 會自動 JOIN Members 表
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status = OrderStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", length = 20)
    private PaymentType paymentType;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 在建構(儲存)時自動填入當前時間
    @PrePersist   // <- 在 JPA save() 之前自動執行
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }
}
