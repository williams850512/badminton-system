package com.badminton.pickupgame;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.badminton.court.Courts;
import com.badminton.member.Members;
import com.badminton.pickupgame.dto.CreatePickupGameRequest;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  // Lombok 自動產生建構子注入（取代 @Autowired）
public class PickupGamesService {

	private final PickupGameRepository pickupRepo;
	private final EntityManager entityManager;

	// ===== 查詢 =====

	/** 查所有揪團 */
	public List<PickupGames> findAll() {
		return pickupRepo.findAll();
	}

	/** 查所有 open 狀態的揪團 */
	public List<PickupGames> findAllOpen() {
		return pickupRepo.findByStatus("open");
	}

	/** 查單一揪團 */
	public PickupGames findById(Integer id) {
		return pickupRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到揪團 ID: " + id));
	}

	// ===== 建立揪團 =====

	/**
	 * 建立揪團（對應舊的 createAndJoin 的「建立」部分）
	 * 報名部分等 PickupGameSignup 做好後再加
	 */
	@Transactional
	public PickupGames createGame(CreatePickupGameRequest req) {

		// 1. 檢查同場地同時段是否已有揪團
		if (pickupRepo.existsCourtTimeConflict(
				req.getCourtId(), req.getGameDate(),
				req.getStartTime(), req.getEndTime())) {
			throw new RuntimeException("該場地在此時段已有揪團，無法重複開團。");
		}

		// 2. 檢查主揪在同時段是否已有開團
		if (pickupRepo.existsHostTimeConflict(
				req.getHostId(), req.getGameDate(),
				req.getStartTime(), req.getEndTime())) {
			throw new RuntimeException("您在此時段已有其他揪團，無法重複開團。");
		}

		// 3. 建立揪團 Entity
		//    用 getReference() 只建立代理物件，不會真正 SELECT 查詢
		//    等合併隊友的 Members/Courts Entity 後就能正常運作
		PickupGames game = new PickupGames();
		game.setHost(entityManager.getReference(Members.class, req.getHostId()));
		game.setCourt(entityManager.getReference(Courts.class, req.getCourtId()));
		game.setGameDate(req.getGameDate());
		game.setStartTime(req.getStartTime());
		game.setEndTime(req.getEndTime());
		game.setMaxPlayers(req.getMaxPlayers());
		game.setSkillLevel(req.getSkillLevel());
		game.setFeePerPerson(req.getFeePerPerson());
		game.setDescription(req.getDescription());
		// currentPlayers, status, createdAt 由 @PrePersist 自動處理

		return pickupRepo.save(game);
	}

	// ===== 取消揪團 =====

	/**
	 * 取消揪團（簡化版：先不驗主揪身份，等 Signup 表做好再補）
	 */
	@Transactional
	public PickupGames cancelGame(Integer gameId) {
		PickupGames game = findById(gameId);

		if ("cancelled".equals(game.getStatus())) {
			throw new RuntimeException("此揪團已經被取消。");
		}

		game.setStatus("cancelled");
		return pickupRepo.save(game);
	}

	// ===== 更新揪團 =====

	@Transactional
	public PickupGames updateGame(Integer gameId, CreatePickupGameRequest req) {
		PickupGames game = findById(gameId);

		if (!"open".equals(game.getStatus())) {
			throw new RuntimeException("只有 open 狀態的揪團才能修改。");
		}

		// 若場地或時間有變動，重新檢查衝突
		boolean timeChanged = !game.getGameDate().equals(req.getGameDate())
				|| !game.getStartTime().equals(req.getStartTime())
				|| !game.getEndTime().equals(req.getEndTime());
		boolean courtChanged = !game.getCourt().getCourtId().equals(req.getCourtId());

		if (timeChanged || courtChanged) {
			if (pickupRepo.existsCourtTimeConflict(
					req.getCourtId(), req.getGameDate(),
					req.getStartTime(), req.getEndTime())) {
				throw new RuntimeException("該場地在此時段已有揪團，無法修改。");
			}
		}

		// 更新欄位
		game.setCourt(entityManager.getReference(Courts.class, req.getCourtId()));
		game.setGameDate(req.getGameDate());
		game.setStartTime(req.getStartTime());
		game.setEndTime(req.getEndTime());
		game.setMaxPlayers(req.getMaxPlayers());
		game.setSkillLevel(req.getSkillLevel());
		game.setFeePerPerson(req.getFeePerPerson());
		game.setDescription(req.getDescription());

		return pickupRepo.save(game);
	}
}
