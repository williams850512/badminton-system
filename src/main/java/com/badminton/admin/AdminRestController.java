package com.badminton.admin;

import com.badminton.member.Member;
import com.badminton.member.MemberService;
import com.badminton.member.MemberStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
public class AdminRestController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private MemberService memberService;

    // 管理員登入
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        return adminService.login(loginData.get("username"), loginData.get("password"))
            .<ResponseEntity<?>>map(admin -> {
                session.setAttribute("adminUser", admin);
                return ResponseEntity.ok(admin);
            })
            .orElse(ResponseEntity.status(401).body("帳號或密碼錯誤"));
    }

    // 取得所有管理員清單
    @GetMapping("/list")
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    // 根據 ID 取得單一管理員
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable int id) {
        return adminService.getAdminById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 新增管理員
    @PostMapping("/add")
    public ResponseEntity<?> addAdmin(@RequestBody Admin admin) {
        try {
            Admin savedAdmin = adminService.addAdmin(admin);
            return ResponseEntity.ok(savedAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 修改管理員資料
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable int id, @RequestBody Admin admin) {
        admin.setAdminId(id);
        Admin updated = adminService.updateAdmin(admin);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // 刪除管理員
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable int id) {
        try {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok("管理員刪除成功");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("刪除失敗：找不到該管理員");
        }
    }

    // 搜尋管理員
    @GetMapping("/search")
    public List<Admin> searchAdmins(@RequestParam String keyword) {
        return adminService.searchAdmins(keyword);
    }

    // 修改管理員備註
    @PatchMapping("/{id}/note")
    public ResponseEntity<?> updateAdminNote(@PathVariable int id, @RequestBody Map<String, String> data) {
        boolean success = adminService.updateAdminNote(id, data.get("note"));
        return success ? ResponseEntity.ok("備註更新成功") : ResponseEntity.badRequest().body("更新失敗");
    }

    // ================= Member Management =================
    @GetMapping("/member")
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("/member/search")
    public List<Member> searchMembers(@RequestParam String keyword) {
        return memberService.searchMembers(keyword);
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable int id) {
        return memberService.getMemberById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/member/{id}")
    public ResponseEntity<?> updateMemberByAdmin(@PathVariable int id, @RequestBody Member member) {
        member.setMemberId(id);
        Member updated = memberService.updateMemberByAdmin(member);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/member/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable int id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok("會員刪除成功");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("刪除失敗：找不到該會員");
        }
    }

    @PatchMapping("/member/{id}/note")
    public ResponseEntity<?> updateMemberNote(@PathVariable int id, @RequestBody Map<String, String> data) {
        boolean success = memberService.updateNote(id, data.get("note"));
        return success ? ResponseEntity.ok("備註更新成功") : ResponseEntity.badRequest().body("更新失敗");
    }

    // 變更管理員狀態
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateAdminStatus(@PathVariable int id, @RequestBody Map<String, String> data) {
        return adminService.getAdminById(id).map(admin -> {
            admin.setStatus(AdminStatus.valueOf(data.get("status")));
            adminService.updateAdmin(admin);
            return ResponseEntity.ok("狀態更新成功");
        }).orElse(ResponseEntity.notFound().build());
    }

    // 變更會員狀態
    @PatchMapping("/member/{id}/status")
    public ResponseEntity<?> updateMemberStatus(@PathVariable int id, @RequestBody Map<String, String> data) {
        return memberService.getMemberById(id).map(member -> {
            member.setStatus(MemberStatus.valueOf(data.get("status")));
            memberService.updateMemberByAdmin(member);
            return ResponseEntity.ok("狀態更新成功");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("已登出");
    }
}
