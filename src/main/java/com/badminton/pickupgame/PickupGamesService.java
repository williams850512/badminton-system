package com.badminton.pickupgame;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PickupGamesService {

	@Autowired
	private PickupGameRepository pickupGameRepo;

	@Autowired
	private PickupGameSignupsRepository signupsRepo;

	// ===== 查詢 =====

	/** 查詢全部揪團（同時校正 currentPlayers，確保與報名紀錄一致） */
	public List<PickupGames> findAll() {
		List<PickupGames> games = pickupGameRepo.findAllByOrderByGameDateAscStartTimeAsc();
		// 每一場揪團都即時校正人數
		games.forEach(this::syncCurrentPlayers);
		return games;
	}

	/** 根據 ID 查詢單一揪團 */
	public PickupGames findById(Integer id) {
		PickupGames game = pickupGameRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到揪團 ID: " + id));
		syncCurrentPlayers(game);
		return game;
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

	// ===== 私有方法：校正 currentPlayers =====

	/**
	 * 從報名表 COUNT 出 JOINED 狀態的實際人數，
	 * 加上主揪 1 人，寫回 currentPlayers。
	 * 如果跟 DB 值不同才做 UPDATE，避免無謂寫入。
	 */
	private void syncCurrentPlayers(PickupGames game) {
		int joinedCount = signupsRepo.countByGame_GameIdAndStatus(
				game.getGameId(), SignupStatus.JOINED);
		int realTotal = 1 + joinedCount; // 主揪 + 已加入的報名

		if (game.getCurrentPlayers() != realTotal) {
			game.setCurrentPlayers(realTotal);
			pickupGameRepo.save(game);
		}
	}
}

