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

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementRestController {
	
	@Autowired
	private AnnouncementService aService;
	
	// GET /api/announcements
	@GetMapping
	public List<Announcement> findAll(){
		return aService.findAll();
	}
	
	// GET /api/announcements/2
	@GetMapping("/{id}")
	public Announcement findById(@PathVariable Integer id) {
		return aService.findById(id);
	}
	
	// POST /api/announcements (Body: {"title":"緊急通知", ...})
	// 【邏輯說明】
	// 1. 前端送來的 JSON 只包含公告內容（title, content, status, category...），不包含 admin
	// 2. 從 HttpSession 取出目前登入的管理員物件 (Admin)
	// 3. 將 Admin 物件傳給 Service，Service 會把它設定到公告的 @ManyToOne admin 欄位
	// 4. JPA save() 時，Hibernate 會自動把 admin.adminId 寫入 Announcements 表的 admin_id 外鍵
	@PostMapping
	public Announcement create(@RequestBody Announcement announcement, HttpSession session) {
		Admin sessionAdmin = (Admin) session.getAttribute("adminUser");
		return aService.save(announcement, sessionAdmin);
	}
	
	// PUT /api/announcements/2
	// 【邏輯說明】
	// 更新時也需要傳入 session 中的管理員，
	// 因為前端可能沒有送 admin 欄位，Service 會判斷若為 null 就沿用 Session 中的管理員
	@PutMapping("/{id}")
	public Announcement update(@PathVariable Integer id, @RequestBody Announcement announcement, HttpSession session) {
		announcement.setAnnouncementId(id);
		Admin sessionAdmin = (Admin) session.getAttribute("adminUser");
		return aService.save(announcement, sessionAdmin);
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

}
