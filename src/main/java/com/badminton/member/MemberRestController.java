package com.badminton.member;

import com.badminton.config.JwtUtil;
import com.badminton.member.dto.MemberResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.badminton.notification.NotificationMessage;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberRestController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 1. 會員登入（回傳 JWT Token）
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        return memberService.login(username, password)
            .<ResponseEntity<?>>map(user -> {
                String token = jwtUtil.generateToken(user.getMemberId(), user.getUsername(), "MEMBER");
                Map<String, Object> result = new HashMap<>();
                result.put("token", token);
                result.put("member", new MemberResponseDTO(user));
                return ResponseEntity.ok(result);
            })
            .orElse(ResponseEntity.status(401).body("帳號或密碼錯誤"));
    }

    // 1.5 Google 第三方登入
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody com.badminton.member.dto.GoogleLoginRequestDTO request) {
        try {
            // 注意：這裡的 Client ID 必須替換為您在 Google Cloud Console 申請的真實 ID
            String CLIENT_ID = "901309102855-shi28efflfnhvjkjo27askl6ntt3tqv9.apps.googleusercontent.com"; 

            com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier verifier = 
                new com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder(
                    new com.google.api.client.http.javanet.NetHttpTransport(), 
                    new com.google.api.client.json.gson.GsonFactory())
                .setAudience(java.util.Collections.singletonList(CLIENT_ID))
                .build();

            com.google.api.client.googleapis.auth.oauth2.GoogleIdToken idToken = verifier.verify(request.getCredential());
            if (idToken != null) {
                com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload payload = idToken.getPayload();

                String googleId = payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                // 呼叫 Service 執行綁定或自動註冊
                Member user = memberService.googleLogin(googleId, email, name, pictureUrl);

                // 核發系統的 JWT Token
                String token = jwtUtil.generateToken(user.getMemberId(), user.getUsername(), "MEMBER");
                Map<String, Object> result = new HashMap<>();
                result.put("token", token);
                result.put("member", new MemberResponseDTO(user));
                return ResponseEntity.ok(result);

            } else {
                return ResponseEntity.status(401).body(java.util.Map.of("message", "無效的 Google Token"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("message", "伺服器驗證失敗：" + e.getMessage()));
        }
    }

    // 2. 註冊 新增會員
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Member member) {
        try {
            if (member.getUsername() == null || !member.getUsername().matches("^[A-Za-z0-9]{6,12}$")) {
                return ResponseEntity.badRequest().body("帳號必須為 6-12 碼英數字 (不可包含特殊字元)");
            }
            if (member.getPassword() == null || member.getPassword().length() < 6 || member.getPassword().length() > 12) {
                return ResponseEntity.badRequest().body("密碼必須為 6-12 個字元");
            }
            if (member.getBirthday() != null && member.getBirthday().isAfter(java.time.LocalDate.now())) {
                return ResponseEntity.badRequest().body("生日不能設定為未來的日期");
            }

            Member savedMember = memberService.register(member);
            
            // 廣播推播通知給後台管理員
            NotificationMessage msg = new NotificationMessage(
                    UUID.randomUUID().toString(),
                    "新會員註冊",
                    "有新會員「" + savedMember.getUsername() + "」剛剛註冊成功囉！",
                    LocalDateTime.now()
            );
            messagingTemplate.convertAndSend("/topic/admin/notifications", msg);

            return ResponseEntity.ok(new MemberResponseDTO(savedMember));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. 搜尋
    @GetMapping("/search")
    public List<MemberResponseDTO> search(@RequestParam String keyword) {
        return memberService.searchMembers(keyword).stream()
                .map(MemberResponseDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    // 4. 取得個人資料（從 JWT 的 request attribute 取得 userId）
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("jwtUserId");
        if (userId == null) {
            return ResponseEntity.status(401).body("未登入");
        }
        return memberService.getMemberById(userId)
                .map(MemberResponseDTO::new)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. 更新資料（從 JWT 的 request attribute 取得 userId）
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Member member, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("jwtUserId");
        if (userId == null) {
            return ResponseEntity.status(401).body("請重新登入後再修改");
        }
        member.setMemberId(userId);
        Member updated = memberService.updateMember(member);
        if (updated != null) {
            return ResponseEntity.ok(new MemberResponseDTO(updated));
        }
        return ResponseEntity.badRequest().body("更新失敗");
    }

    // 6. 登出（JWT 是 stateless，前端刪除 token 即可）
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("已成功登出");
    }

    // 7. 忘記密碼 — 驗證身份後重設密碼
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String email = data.get("email");
        String birthday = data.get("birthday");
        String newPassword = data.get("newPassword");

        if (username == null || email == null || birthday == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "所有欄位都必須填寫"));
        }
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "新密碼至少需要 6 個字元"));
        }

        boolean success = memberService.resetPassword(username, email, birthday, newPassword);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "密碼已重設成功，請使用新密碼登入"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "驗證失敗，請確認帳號、信箱與生日是否正確"));
    }
}
