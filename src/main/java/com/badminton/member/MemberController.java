package com.badminton.member;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService; // 改為呼叫 Service 層，確保邏輯與舊專案一致

    // ✅ 1. 登入 (保留 COLLATE 驗證與登入時間更新邏輯)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        return memberService.login(username, password)
            .<ResponseEntity<?>>map(user -> {
                // Service 內已包含更新最後登入時間的邏輯
                session.setAttribute("user", user);
                return ResponseEntity.ok(user);
            })
            .orElse(ResponseEntity.status(401).body("帳號或密碼錯誤"));
    }

    // ✅ 2. 註冊 (保留 isUsernameExists 檢查與預設值設定)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody MemberBean member) {
        try {
            // Service 內已包含重複帳號檢查與狀態/等級預設值設定
            MemberBean savedMember = memberService.register(member);
            return ResponseEntity.ok(savedMember);
        } catch (RuntimeException e) {
            // 抓取 Service 拋出的「帳號已存在」等錯誤訊息
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ 3. 搜尋 (保留中文關鍵字轉換邏輯與 9 欄位比對)
    @GetMapping("/search")
    public List<MemberBean> search(@RequestParam String keyword) {
        // 邏輯封裝在 Service 內，支援「正常/停權」等關鍵字轉換
        return memberService.searchMembers(keyword);
    }

    // ✅ 4. 取得個人資料 (用於登入後的前端頁面載入)
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        MemberBean currentUser = (MemberBean) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("未登入");
        }
        
        return memberService.getMemberById(currentUser.getMemberId())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 5. 更新個人資料 (保留 Session 安全檢查，防止竄改 ID)
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody MemberBean member, HttpSession session) {
        MemberBean currentUser = (MemberBean) session.getAttribute("user");
        
        // 強制使用 Session 中的 ID，確保使用者只能修改自己的資料
        if (currentUser != null) {
            member.setMemberId(currentUser.getMemberId());
        } else {
            return ResponseEntity.status(401).body("請重新登入後再修改");
        }
        
        MemberBean updated = memberService.updateMember(member);
        if (updated != null) {
            session.setAttribute("user", updated); // 更新 Session 中的資料
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.badRequest().body("更新失敗");
    }

    // ✅ 6. 登出 (清除 Session)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("已成功登出");
    }
    
    @GetMapping("/all")
    public List<MemberBean> getAllMembers() {
        return memberService.getAllMembers();
    }
}
