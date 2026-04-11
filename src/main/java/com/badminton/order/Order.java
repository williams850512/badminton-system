package com.badminton.order;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    // 這是在舊版 OrderBean 中的欄位，但通常這不會直接存儲在 Orders 表中
    // 而是透過 View 或是 JOIN 查詢得到。在 Entity 映射時，我們若只是要拿來裝載查詢結果
    // 或是暫時存放，加上 @Transient 就不會把它當成資料庫的實際欄位去檢查。
    @Transient 
    private String memberName;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "payment_type", length = 50)
    private String paymentType;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
