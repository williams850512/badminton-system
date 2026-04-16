package com.badminton.announcement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	public Announcement save(Announcement announcement) {
		// 目前尚未實作登入，先預設 adminId = 1（之後接登入系統再改）
		if (announcement.getAdminId() == null) {
			announcement.setAdminId(1);
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
