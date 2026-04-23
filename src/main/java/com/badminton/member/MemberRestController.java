package com.badminton.member;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    // 1. 登入
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        return memberService.login(username, password)
            .<ResponseEntity<?>>map(user -> {
                session.setAttribute("user", user);
                return ResponseEntity.ok(user);
            })
            .orElse(ResponseEntity.status(401).body("帳號或密碼錯誤"));
    }

    // 2. 註冊 新增會員
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Member member) {
        try {
            Member savedMember = memberService.register(member);
            return ResponseEntity.ok(savedMember);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. 搜尋
    @GetMapping("/search")
    public List<Member> search(@RequestParam String keyword) {
        return memberService.searchMembers(keyword);
    }

    // 4. 取得個人資料
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        Member currentUser = (Member) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("未登入");
        }
        return memberService.getMemberById(currentUser.getMemberId())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. 更新資料
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Member member, HttpSession session) {
        Member currentUser = (Member) session.getAttribute("user");
        if (currentUser != null) {
            member.setMemberId(currentUser.getMemberId());
        } else {
            return ResponseEntity.status(401).body("請重新登入後再修改");
        }
        Member updated = memberService.updateMember(member);
        if (updated != null) {
            session.setAttribute("user", updated);
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.badRequest().body("更新失敗");
    }

    // 6. 登出
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("已成功登出");
    }
}
