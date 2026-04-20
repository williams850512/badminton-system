package com.badminton.venue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class VenueService {

	@Autowired
	private VenueRepository venueRepo;

	// ===== 查詢 =====

	/** 查詢全部場館 */
	public List<Venue> findAll() {
		return venueRepo.findAll();
	}

	/** 根據 ID 查詢單一場館 */
	public Venue findById(Integer id) {
		return venueRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到場館 ID: " + id));
	}

	// ===== 新增 / 更新 =====

	/** 新增或更新場館（有 ID 就 UPDATE，沒有就 INSERT） */
	public Venue save(Venue venue) {
		return venueRepo.save(venue);
	}

	// ===== 刪除 =====

	/** 根據 ID 刪除場館 */
	public void deleteById(Integer id) {
		venueRepo.deleteById(id);
	}

	// ===== 狀態更新 =====

	/** 更改場館狀態（營業中 / 停用 / 整修中） */
	public Venue updateStatus(Integer id, VenueStatus newStatus) {
		// 1. 先從資料庫撈出這筆 Venue
		Venue venue = venueRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到場館 ID: " + id));

		// 2. 只修改 status 欄位
		venue.setStatus(newStatus);

		// 3. 存回資料庫（Hibernate 會自動產生 UPDATE SQL）
		return venueRepo.save(venue);
	}

}
