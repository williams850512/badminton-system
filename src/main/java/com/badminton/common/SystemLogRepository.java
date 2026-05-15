package com.badminton.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    // 依操作類型查詢
    List<SystemLog> findByActionOrderByCreatedAtDesc(String action);

    // 依日期範圍查詢
    @Query("SELECT s FROM SystemLog s WHERE s.createdAt BETWEEN :start AND :end ORDER BY s.createdAt DESC")
    List<SystemLog> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 依操作類型 + 日期範圍查詢
    @Query("SELECT s FROM SystemLog s WHERE s.action = :action AND s.createdAt BETWEEN :start AND :end ORDER BY s.createdAt DESC")
    List<SystemLog> findByActionAndDateRange(@Param("action") String action, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 取得所有日誌（按時間倒序）
    List<SystemLog> findAllByOrderByCreatedAtDesc();

    // 關鍵字搜尋（操作者名稱、對象名稱、操作細節）
    @Query("SELECT s FROM SystemLog s WHERE " +
           "s.operatorName LIKE %:kw% OR " +
           "s.targetName LIKE %:kw% OR " +
           "s.details LIKE %:kw% OR " +
           "s.action LIKE %:kw% " +
           "ORDER BY s.createdAt DESC")
    List<SystemLog> searchLogs(@Param("kw") String keyword);
}
