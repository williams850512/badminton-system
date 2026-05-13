package com.badminton.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具類
 *
 * 功能：
 * 1. generateToken() — 產生 JWT（含 userId, username, role）
 * 2. parseToken()    — 解析 JWT 回傳 Claims
 * 3. isExpired()     — 判斷 Token 是否過期
 *
 * 使用方式（在 Controller 裡）：
 *   @Autowired JwtUtil jwtUtil;
 *   String token = jwtUtil.generateToken(1, "admin01", "MANAGER");
 *   Claims claims = jwtUtil.parseToken(token);
 */
@Component
public class JwtUtil {

    // 密鑰（至少 32 字元，給 HS256 用）
    private static final String SECRET = "YuGuoTianQing-Badminton-2026-JWT-SecretKey!!";

    // Token 有效期：24 小時
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L;

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 產生 JWT Token
     *
     * @param userId   使用者 ID（admin_id 或 member_id）
     * @param username 帳號
     * @param role     角色（如 "MANAGER", "STAFF", "MEMBER"）
     * @return JWT 字串
     */
    public String generateToken(int userId, String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * 解析 JWT Token，回傳 Claims
     * 如果 Token 無效或過期，會拋出例外
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 判斷 Token 是否過期
     */
    public boolean isExpired(String token) {
        try {
            return parseToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
