package com.badminton.admin;

import com.badminton.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 儀表板統計 API
 * 提供後台 Dashboard 需要的會員相關統計數據
 */
@RestController
@RequestMapping("/api/admins/dashboard")
public class DashboardController {

    @Autowired
    private MemberRepository memberRepo;

    /**
     * 1. 總覽卡片數據
     *    回傳：總會員數、今日新增、本週新增、本月新增
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalMembers", memberRepo.count());
        stats.put("todayNew", memberRepo.countTodayNewMembers());
        stats.put("weekNew", memberRepo.countWeekNewMembers());
        stats.put("monthNew", memberRepo.countMonthNewMembers());
        return ResponseEntity.ok(stats);
    }

    /**
     * 2. 男女比例統計
     *    回傳：[{ label: "男", value: 120 }, { label: "女", value: 80 }]
     */
    @GetMapping("/gender")
    public ResponseEntity<?> getGenderDistribution() {
        List<Object[]> results = memberRepo.countByGender();
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", row[0] != null ? row[0].toString() : "未填寫");
            item.put("value", ((Number) row[1]).intValue());
            data.add(item);
        }
        return ResponseEntity.ok(data);
    }

    /**
     * 3. 年齡分佈統計
     *    回傳：[{ label: "21-30歲", value: 50 }, ...]
     */
    @GetMapping("/age")
    public ResponseEntity<?> getAgeDistribution() {
        List<Object[]> results = memberRepo.getAgeDistribution();
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", row[0].toString());
            item.put("value", ((Number) row[1]).intValue());
            data.add(item);
        }
        return ResponseEntity.ok(data);
    }

    /**
     * 4. 會員成長趨勢（近 12 個月）
     *    回傳：[{ month: "2026-01", count: 15 }, ...]
     */
    @GetMapping("/growth")
    public ResponseEntity<?> getMonthlyGrowth() {
        List<Object[]> results = memberRepo.getMonthlyGrowth();
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", row[0].toString());
            item.put("count", ((Number) row[1]).intValue());
            data.add(item);
        }
        return ResponseEntity.ok(data);
    }

    /**
     * 5. 會員等級分佈
     *    回傳：[{ label: "NORMAL", value: 200 }, { label: "VIP", value: 30 }]
     */
    @GetMapping("/level")
    public ResponseEntity<?> getLevelDistribution() {
        List<Object[]> results = memberRepo.countByMembershipLevel();
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", row[0].toString());
            item.put("value", ((Number) row[1]).intValue());
            data.add(item);
        }
        return ResponseEntity.ok(data);
    }
}
