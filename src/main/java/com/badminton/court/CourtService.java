package com.badminton.court;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourtService {
	
	@Autowired
	private CourtRepository courtRepo;
	
	// ===== 查詢 =====
	
	public List<Court> findAll(){
		return courtRepo.findAll();
	}
	
	public Court findById(Integer id) {
		return courtRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到球場 ID: " + id));
	}
	
	// ===== 新增 / 更新 =====
	
	public Court save(Court court) {
		return courtRepo.save(court);
	}
	
	// ===== 刪除 =====
	public void deleteById(Integer id) {
		courtRepo.deleteById(id);
	}
	
	// ===== 狀態更新 =====
	public Court updateStatus(Integer id, CourtStatus newStatus) {
		// 1. 先從資料庫撈出這筆 Court
		Court court = courtRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到球場 ID: " + id));
		
		// 2. 只修改 status 欄位
		court.setStatus(newStatus);
		
		// 3. 存回資料庫（Hibernate 會自動產生 UPDATE SQL）
		return courtRepo.save(court);
	}

}
