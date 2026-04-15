package com.badminton.pickupgame;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PickupGamesService {

	@Autowired
	private PickupGameRepository pickupGameRepo;

	// ===== 查詢 =====

	/** 查詢全部揪團 */
	public List<PickupGames> findAll() {
		return pickupGameRepo.findAll();
	}

	/** 根據 ID 查詢單一揪團 */
	public PickupGames findById(Integer id) {
		return pickupGameRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到揪團 ID: " + id));
	}

	// ===== 新增 / 更新 =====

	/** 新增或更新揪團（有 ID 就 UPDATE，沒有就 INSERT） */
	public PickupGames save(PickupGames game) {
		return pickupGameRepo.save(game);
	}

	// ===== 刪除 =====

	/** 根據 ID 刪除揪團 */
	public void deleteById(Integer id) {
		pickupGameRepo.deleteById(id);
	}
}
