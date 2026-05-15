package com.badminton.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 系統日誌服務
 * 提供簡易的 log() 方法，方便各 Service / Controller 記錄操作
 */
@Service
public class SystemLogService {

    @Autowired
    private SystemLogRepository logRepo;

    /**
     * 記錄一筆操作日誌
     */
    @Transactional
    public void log(String operatorType, Integer operatorId, String operatorName,
                    String action, String targetType, Integer targetId,
                    String targetName, String details) {
        SystemLog log = new SystemLog();
        log.setOperatorType(operatorType);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        
        // 強制修復：如果是登入或登出，且 targetName 為空或為「系統」，則自動帶入 operatorName
        String finalTargetName = targetName;
        if (("LOGIN".equals(action) || "LOGOUT".equals(action)) && (finalTargetName == null || "系統".equals(finalTargetName))) {
            finalTargetName = operatorName;
        }
        log.setTargetName(finalTargetName != null ? finalTargetName : "系統");
        
        log.setDetails(details);
        logRepo.save(log);
    }

    /**
     * 取得所有日誌（按時間倒序）
     */
    public List<SystemLog> getAllLogs() {
        return logRepo.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 依篩選條件查詢日誌
     */
    public List<SystemLog> getLogs(String action, LocalDate startDate, LocalDate endDate) {
        boolean hasAction = action != null && !action.isEmpty();
        boolean hasDateRange = startDate != null && endDate != null;

        if (hasAction && hasDateRange) {
            return logRepo.findByActionAndDateRange(
                action,
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
            );
        } else if (hasAction) {
            return logRepo.findByActionOrderByCreatedAtDesc(action);
        } else if (hasDateRange) {
            return logRepo.findByDateRange(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
            );
        } else {
            return logRepo.findAllByOrderByCreatedAtDesc();
        }
    }

    /**
     * 關鍵字搜尋
     */
    public List<SystemLog> searchLogs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return logRepo.findAllByOrderByCreatedAtDesc();
        }
        return logRepo.searchLogs(keyword.trim());
    }
}
