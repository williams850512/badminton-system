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

	// ===== 新增 / 更新 =====

	/** 新增或更新報名紀錄（有 ID 就 UPDATE，沒有就 INSERT），必須有驗證流程 */
	
	public PickupGameSignups save(PickupGameSignups signup) {
		
	// ====== ① 查出揪團，確認存在 ======
		Integer signupid = signup.getGame().getGameId();
		PickupGames game =  pickupGameRepo.findById(signupid)
		.orElseThrow(()-> new RuntimeException("找不到揪團 ID: " + signupid));
		
	// ====== ② 檢查揪團狀態是否為 OPEN ======
	if(game.getStatus() != PickupGameStatus.OPEN) {
		throw new RuntimeException("此揪團目前不開放報名" );
		
	}
	
	// ====== ③ 檢查是否重複報名 ======
	if(signupsRepo.existsByGame_GameIdAndMember_MemberId(signupid, signup.getMember().getMemberId())) {
		throw new RuntimeException("您已經報名過此揪團");
		
	}
	
	// ====== ④ 儲存報名 + 更新揪團人數 ======
	// a. 先儲存報名紀錄
	PickupGameSignups saved = signupsRepo.save(signup);
	
	// b. 揪團的 currentPlayers + 1
	game.setCurrentPlayers(game.getCurrentPlayers()+ 1);
	
	// c. 如果人數已滿 (currentPlayers >= maxPlayers)，把 status 改成 FULL
	if(game.getCurrentPlayers() >= game.getMaxPlayers()) {
		game.setStatus(PickupGameStatus.FULL);
	}
	
	// d. 存回揪團
	pickupGameRepo.save(game);
	
		
		return saved;
	}

	// ===== 刪除 =====

	/** 根據 ID 刪除報名紀錄 */
	public void deleteById(Integer id) {
		signupsRepo.deleteById(id);
	}
}
