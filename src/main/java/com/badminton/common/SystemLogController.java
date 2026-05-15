package com.badminton.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 操作日誌 API
 * 提供後台查詢系統操作紀錄的介面
 */
@RestController
@RequestMapping("/api/admins/logs")
public class SystemLogController {

    @Autowired
    private SystemLogService logService;

    /**
     * 取得日誌列表（支援篩選）
     * @param action    操作類型篩選（可選）
     * @param startDate 起始日期（可選）
     * @param endDate   結束日期（可選）
     */
    @GetMapping
    public ResponseEntity<List<SystemLog>> getLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<SystemLog> logs = logService.getLogs(action, startDate, endDate);
        return ResponseEntity.ok(logs);
    }

    /**
     * 關鍵字搜尋日誌
     */
    @GetMapping("/search")
    public ResponseEntity<List<SystemLog>> searchLogs(@RequestParam String keyword) {
        List<SystemLog> logs = logService.searchLogs(keyword);
        return ResponseEntity.ok(logs);
    }
}
