package com.badminton.admin;

import com.badminton.member.MemberBean;
import com.badminton.member.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService; // 處理管理員自己的邏輯

    @Autowired
    private MemberService memberService; // 處理管理員「去動一般會員」的邏輯

    // ==========================================
    // 1. 管理員自我管理 (Admin Self-Management)
    // ==========================================

    // ✅ 管理員登入 (邏輯已封裝在 Service，包含 COLLATE 檢查與時間更新)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        return adminService.login(loginData.get("username"), loginData.get("password"))
            .<ResponseEntity<?>>map(admin -> {
                session.setAttribute("adminUser", admin);
                return ResponseEntity.ok(admin);
            })
            .orElse(ResponseEntity.status(401).body("帳號或密碼錯誤"));
    }

    // ✅ 取得所有管理員清單
    @GetMapping("/list")
    public List<AdminBean> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    // ✅ 根據 ID 取得單一管理員
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable int id) {
        return adminService.getAdminById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 新增管理員 (包含帳號重複檢查邏輯)
    @PostMapping("/add")
    public ResponseEntity<?> addAdmin(@RequestBody AdminBean admin) {
        try {
            AdminBean savedAdmin = adminService.addAdmin(admin);
            return ResponseEntity.ok(savedAdmin);
        } catch (RuntimeException e) {
            // 這裡會抓到 Service 拋出的 "帳號已存在" 訊息
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ 修改管理員資料
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable int id, @RequestBody AdminBean admin) {
        admin.setAdminId(id);
        AdminBean updated = adminService.updateAdmin(admin);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // ✅ 刪除管理員
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable int id) {
        try {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok("管理員刪除成功");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("刪除失敗：找不到該管理員");
        }
    }

    // ✅ 搜尋管理員
    @GetMapping("/search")
    public List<AdminBean> searchAdmins(@RequestParam String keyword) {
        return adminService.searchAdmins(keyword);
    }

    // ✅ 修改管理員備註
    @PatchMapping("/{id}/note")
    public ResponseEntity<?> updateAdminNote(@PathVariable int id, @RequestBody Map<String, String> data) {
        boolean success = adminService.updateAdminNote(id, data.get("note"));
        return success ? ResponseEntity.ok("備註更新成功") : ResponseEntity.badRequest().body("更新失敗");
    }

    // ==========================================
    // 2. 管理員管理一般會員 (Admin Managing Members)
    // ==========================================

    // 取得所有會員 (原本 DAO 的 getAllMembers)
    @GetMapping("/members")
    public List<MemberBean> getAllMembers() {
        return memberService.getAllMembers();
    }

    // 搜尋會員 (原本 DAO 的 searchMembers，支援 9 欄位模糊搜尋)
    @GetMapping("/members/search")
    public List<MemberBean> searchMembers(@RequestParam String keyword) {
        return memberService.searchMembers(keyword);
    }

    // 根據 ID 取得單一會員 (給 editMember 頁面使用)
    @GetMapping("/members/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable int id) {
        return memberService.getMemberById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 修改會員資料 (管理員用，可修改 status/membershipLevel 等)
    @PutMapping("/members/{id}")
    public ResponseEntity<?> updateMemberByAdmin(@PathVariable int id, @RequestBody MemberBean member) {
        member.setMemberId(id);
        MemberBean updated = memberService.updateMemberByAdmin(member);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // 刪除會員 (原本 DAO 的 deleteMember)
    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable int id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok("會員刪除成功");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("刪除失敗：找不到該會員");
        }
    }

    // 修改會員備註 (原本 DAO 的 updateNote)
    @PatchMapping("/members/{id}/note")
    public ResponseEntity<?> updateMemberNote(@PathVariable int id, @RequestBody Map<String, String> data) {
        boolean success = memberService.updateNote(id, data.get("note"));
        return success ? ResponseEntity.ok("備註更新成功") : ResponseEntity.badRequest().body("更新失敗");
    }

    // ==========================================
    // 3. 登出
    // ==========================================

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("已登出");
    }
}
