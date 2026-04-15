package com.badminton.pickupgame;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PickupGameSignupsService {

	@Autowired
	private PickupGameSignupsRepository signupsRepo;

	// ===== 查詢 =====

	/** 查詢全部報名紀錄 */
	public List<PickupGameSignups> findAll() {
		return signupsRepo.findAll();
	}

	/** 根據 ID 查詢單一報名紀錄 */
	public PickupGameSignups findById(Integer id) {
		return signupsRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到報名紀錄 ID: " + id));
	}

	// ===== 新增 / 更新 =====

	/** 新增或更新報名紀錄（有 ID 就 UPDATE，沒有就 INSERT） */
	public PickupGameSignups save(PickupGameSignups signup) {
		return signupsRepo.save(signup);
	}

	// ===== 刪除 =====

	/** 根據 ID 刪除報名紀錄 */
	public void deleteById(Integer id) {
		signupsRepo.deleteById(id);
	}
}
