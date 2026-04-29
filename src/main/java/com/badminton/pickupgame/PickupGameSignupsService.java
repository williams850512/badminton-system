package com.badminton.pickupgame;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PickupGameSignupsService {

	@Autowired
	private PickupGameSignupsRepository signupsRepo;
	
	@Autowired
	private PickupGameRepository pickupGameRepo;

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

	/** 根據揪團 ID 查詢該場所有報名紀錄 */
	public List<PickupGameSignups> findByGameId(Integer gameId){
		return signupsRepo.findByGame_GameId(gameId);
	}
	
	// ===== 新增 / 更新 =====

	/** 新增或更新報名紀錄（有 ID 就 UPDATE，沒有就 INSERT），必須有驗證流程 */
	
	public PickupGameSignups save(PickupGameSignups signup) {
		
	// ====== ① 查出揪團，確認存在 ======
		Integer gameId = signup.getGame().getGameId();
		PickupGames game =  pickupGameRepo.findById(gameId)
		.orElseThrow(()-> new RuntimeException("找不到揪團 ID: " + gameId));
		
	// ====== ② 檢查揪團狀態是否為 OPEN ======
	if(game.getStatus() != PickupGameStatus.OPEN) {
		throw new RuntimeException("此揪團目前不開放報名" );
		
	}
	
	// ====== ③ 檢查是否重複報名 ======
	if(signupsRepo.existsByGame_GameIdAndMember_MemberId(gameId, signup.getMember().getMemberId())) {
		throw new RuntimeException("您已經報名過此揪團");
		
	}
	
	// ====== ④ 儲存報名 ======
	PickupGameSignups saved = signupsRepo.save(signup);
	
	// ====== ⑤ 動態計算人數並同步揪團狀態 ======
	syncGamePlayerCount(game);
		
		return saved;
	}

	// ===== 刪除 =====

	/** 根據 ID 刪除報名紀錄，同時更新揪團人數與狀態 */
	public void deleteById(Integer id) {
		// ① 先查出報名紀錄，取得關聯的揪團
		PickupGameSignups signup = signupsRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到報名紀錄 ID: " + id));

		PickupGames game = signup.getGame();

		// ② 刪除報名紀錄
		signupsRepo.deleteById(id);

		// ③ 動態計算人數並同步揪團狀態
		syncGamePlayerCount(game);
	}
	
	// ===== 共用：同步揪團人數與狀態 =====
	
	/**
	 * 從報名表動態 COUNT 出「JOINED」狀態的人數，
	 * 寫回揪團的 currentPlayers，並自動判斷 FULL / OPEN。
	 * 
	 * 主揪(host)算在 +1 裡面，所以公式是：
	 *   currentPlayers = 1(主揪) + COUNT(JOINED 的報名)
	 */
	private void syncGamePlayerCount(PickupGames game) {
		// 從 DB 即時 COUNT
		int joinedCount = signupsRepo.countByGame_GameIdAndStatus(
				game.getGameId(), SignupStatus.JOINED);
		
		// 主揪 + 報名人數
		int total = 1 + joinedCount;
		game.setCurrentPlayers(total);
		
		// 自動切換狀態：滿人 → FULL，有空位 → OPEN
		if (total >= game.getMaxPlayers()) {
			game.setStatus(PickupGameStatus.FULL);
		} else if (game.getStatus() == PickupGameStatus.FULL) {
			// 只有原本是 FULL 的才改回 OPEN（CANCELLED / CLOSED 不動）
			game.setStatus(PickupGameStatus.OPEN);
		}
		
		pickupGameRepo.save(game);
	}
}

