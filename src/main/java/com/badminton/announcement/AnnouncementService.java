package com.badminton.announcement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badminton.admin.Admin;

@Service
public class AnnouncementService {
	
	@Autowired
	private AnnouncementRepository aRepo;
	
	// ===== 查詢 =====
	
	public List<Announcement> findAll(){
		return aRepo.findAll();
	}
	
	public Announcement findById(Integer id) {
		return aRepo.findById(id)
				.orElseThrow(()-> new RuntimeException("找不到公告ID:" + id));
	}
	
	// ===== 新增 / 更新 =====
	
	/**
	 * 儲存公告（新增或更新）
	 * 
	 * 【邏輯說明】
	 * 1. 由 Controller 從 HttpSession 取出目前登入的管理員 (Admin 物件)
	 * 2. 如果前端沒有送 admin 欄位（admin == null），就由 Controller 補上 Session 中的管理員
	 * 3. 這裡的 admin 是一個 @ManyToOne 關聯物件，JPA 只需要裡面有 adminId，
	 *    save() 時就會自動在 Announcements 表寫入對應的 admin_id 外鍵值
	 * 
	 * @param announcement  前端傳入的公告物件
	 * @param sessionAdmin  從 HttpSession 取出的目前登入管理員（可為 null）
	 */
	public Announcement save(Announcement announcement, Admin sessionAdmin) {
		// 如果前端沒有送 admin（新增公告時通常不會送），就用 Session 中的登入管理員
		if (announcement.getAdmin() == null && sessionAdmin != null) {
			announcement.setAdmin(sessionAdmin);
		}
		return aRepo.save(announcement);
	}
	
	// ===== 刪除 =====
	
	public void deleteById(Integer id) {
	    if (!aRepo.existsById(id)) {
	        throw new RuntimeException("找不到公告 ID:" + id);
	    }
	    aRepo.deleteById(id);
	}
	
	// ===== 狀態更新 =====
	public Announcement updateStatus(Integer id, AnnouncementStatus newStatus) {
		// 1. 先從資料庫撈出這筆 Announcement
		Announcement announcement = aRepo.findById(id)
				.orElseThrow(()->new RuntimeException("找不到公告 ID:" + id));
		// 2. 只修改 status 欄位
		announcement.setStatus(newStatus);
		// 3. 存回資料庫（Hibernate 會自動產生 UPDATE SQL）
		return aRepo.save(announcement);
	}

}
