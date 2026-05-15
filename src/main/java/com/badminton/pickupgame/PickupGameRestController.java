package com.badminton.pickupgame;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.badminton.admin.Admin;
import com.badminton.member.Member;
import com.badminton.pickupgame.PickupGameEmailService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/pickup-games")
public class PickupGameRestController {

	@Autowired
	private PickupGamesService pickupGamesService;
	
	@Autowired
	private PickupGameSignupsService signupsService;

	@Autowired
	private PickupGameEmailService pickupGameEmailService;

	// GET /api/pickup-games
	@GetMapping
	public List<PickupGames> findAll() {
		return pickupGamesService.findAll();
	}

	// GET /api/pickup-games/3
	@GetMapping("/{id}")
	public PickupGames findById(@PathVariable Integer id) {
		return pickupGamesService.findById(id);
	}

	// POST /api/pickup-games  (Body: JSON)
	@PostMapping
	public PickupGames create(@RequestBody PickupGames game) {
		return pickupGamesService.save(game);
	}

	// PUT /api/pickup-games/3
	@PutMapping("/{id}")
	public PickupGames update(@PathVariable Integer id, @RequestBody PickupGames game, HttpSession session) {
		PickupGames oldGame = pickupGamesService.findById(id);

		// 若為「取消揪團」操作
		if (oldGame.getStatus() != PickupGameStatus.CANCELLED && game.getStatus() == PickupGameStatus.CANCELLED) {
			// 安全性檢查：只有後台管理員，或原團主可以取消
			Admin admin = (Admin) session.getAttribute("adminUser");
			Member member = (Member) session.getAttribute("user");

			boolean isAuthorized = false;
			if (admin != null) {
				isAuthorized = true; // 管理員放行
			} else if (member != null && oldGame.getHost() != null 
					&& member.getMemberId().equals(oldGame.getHost().getMemberId())) {
				isAuthorized = true; // 原團主放行
			}

			if (!isAuthorized) {
				throw new RuntimeException("權限不足：您不是該場揪團的團主或管理員");
			}

			return pickupGamesService.cancelGame(id);
		}

		// 一般更新操作
		game.setGameId(id);
		return pickupGamesService.save(game);
	}

	// DELETE /api/pickup-games/3
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		pickupGamesService.deleteById(id);
	}
	
	// GET /api/pickup-games/3/signups → 查詢第 3 場揪團的所有報名
	@GetMapping("/{gameId}/signups")
	public List<PickupGameSignups> findSignupsByGameId(@PathVariable Integer gameId){
		return signupsService.findByGameId(gameId);
	}

	// ============================
	// 🌟 POST /api/pickup-games/{gameId}/broadcast
	// 團主群發公告 Email 給所有已報名球友
	// ============================
	@PostMapping("/{gameId}/broadcast")
	public ResponseEntity<Map<String, Object>> broadcast(
			@PathVariable Integer gameId,
			@RequestBody Map<String, String> body) {

		String message = body.get("message");
		if (message == null || message.trim().isEmpty()) {
			return ResponseEntity.badRequest()
					.body(Map.of("success", false, "error", "公告內容不可為空"));
		}

		// ① 查出揪團資訊（用於信件內文的球局摘要）
		PickupGames game = pickupGamesService.findById(gameId);
		String hostName = game.getHost() != null ? game.getHost().getFullName() : "團主";
		String gameInfo = game.getGameDate() + " " + game.getStartTime() + "-" + game.getEndTime();

		// ② 撈出所有已報名球友的 Email（過濾空值）
		List<PickupGameSignups> signups = signupsService.findByGameId(gameId);
		List<String> emails = signups.stream()
				.filter(s -> s.getMember() != null && s.getMember().getEmail() != null)
				.map(s -> s.getMember().getEmail())
				.filter(email -> !email.trim().isEmpty())
				.collect(Collectors.toList());

		if (emails.isEmpty()) {
			return ResponseEntity.ok(
					Map.of("success", true, "sent", 0, "message", "目前沒有已報名的球友 Email"));
		}

		// ③ 群發 Email
		int sent = pickupGameEmailService.sendBroadcast(emails, hostName, gameInfo, message.trim());

		return ResponseEntity.ok(
				Map.of("success", true, "sent", sent, "total", emails.size()));
	}
}
