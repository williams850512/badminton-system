package com.badminton.common;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系統操作日誌
 * 記錄管理員 / 會員的重要操作行為
 */
@Entity
@Table(name = "system_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    /** 操作者類型：ADMIN / MEMBER */
    @Column(name = "operator_type", length = 20)
    private String operatorType;

    /** 操作者 ID */
    @Column(name = "operator_id")
    private Integer operatorId;

    /** 操作者名稱（快照，防止改名後對不起來） */
    @Column(name = "operator_name", length = 50)
    private String operatorName;

    /** 操作類型：REGISTER, UPDATE_MEMBER, DELETE_MEMBER, UPDATE_STATUS 等 */
    @Column(name = "action", length = 50, nullable = false)
    private String action;

    /** 被操作對象類型：MEMBER, ADMIN */
    @Column(name = "target_type", length = 50)
    private String targetType;

    /** 被操作對象 ID */
    @Column(name = "target_id")
    private Integer targetId;

    /** 被操作對象名稱（快照） */
    @Column(name = "target_name", length = 50)
    private String targetName;

    /** 操作細節描述 */
    @Column(name = "details", length = 500)
    private String details;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
