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

import com.badminton.pickupgame.PickupGameEmailService;

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
	public PickupGames update(@PathVariable Integer id, @RequestBody PickupGames game) {
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
