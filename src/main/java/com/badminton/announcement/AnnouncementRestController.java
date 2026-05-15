package com.badminton.announcement;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.badminton.admin.Admin;
import com.badminton.admin.AdminService;
import com.badminton.config.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementRestController {
	
	@Autowired
	private AnnouncementService aService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private JwtUtil jwtUtil;
	
	// GET /api/announcements（公開，不需要登入）
	@GetMapping
	public List<Announcement> findAll(){
		return aService.findAll();
	}
	
	// GET /api/announcements/2（公開）
	@GetMapping("/{id}")
	public Announcement findById(@PathVariable Integer id) {
		return aService.findById(id);
	}
	
	// POST /api/announcements — 新增公告（需要管理員 JWT）
	@PostMapping
	public Announcement create(@RequestBody Announcement announcement, HttpServletRequest request) {
		Admin currentAdmin = getAdminFromJwt(request);
		return aService.save(announcement, currentAdmin);
	}
	
	// PUT /api/announcements/2 — 更新公告（需要管理員 JWT）
	@PutMapping("/{id}")
	public Announcement update(@PathVariable Integer id, @RequestBody Announcement announcement, HttpServletRequest request) {
		announcement.setAnnouncementId(id);
		Admin currentAdmin = getAdminFromJwt(request);
		return aService.save(announcement, currentAdmin);
	}
	
	// PATCH /api/announcements/2/status (Body: {"status":"PUBLISHED"})
	@PatchMapping("/{id}/status")
	public Announcement updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
		return aService.updateStatus(id, AnnouncementStatus.valueOf(body.get("status")));
	}
	
	// DELETE /api/announcements/2
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		aService.deleteById(id);
	}

	// PATCH /api/announcements/2/view → 瀏覽次數 +1（公開）
	@PatchMapping("/{id}/view")
	public Announcement incrementView(@PathVariable Integer id) {
		return aService.incrementViewCount(id);
	}

	/**
	 * 從 Authorization Header 手動解析 JWT，取得目前登入的管理員
	 * （因為 /api/announcements 不經過全域 JwtInterceptor，需手動解析）
	 */
	private Admin getAdminFromJwt(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("未登入或 Token 已失效，請重新登入");
		}
		String token = authHeader.substring(7);
		try {
			Claims claims = jwtUtil.parseToken(token);
			Integer adminId = claims.get("userId", Integer.class);
			return adminService.getAdminById(adminId)
					.orElseThrow(() -> new RuntimeException("找不到管理員 ID:" + adminId));
		} catch (Exception e) {
			throw new RuntimeException("Token 解析失敗，請重新登入：" + e.getMessage());
		}
	}

}

