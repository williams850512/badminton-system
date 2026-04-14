package com.badminton.pickupgame;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.badminton.pickupgame.dto.CreatePickupGameRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pickup-games")
@RequiredArgsConstructor
public class PickupGameController {

	private final PickupGamesService pickupService;

	// ===== GET 查詢 =====

	/** GET /api/pickup-games — 查所有揪團 */
	@GetMapping
	public ResponseEntity<List<PickupGames>> getAllGames() {
		return ResponseEntity.ok(pickupService.findAll());
	}

	/** GET /api/pickup-games/open — 查所有 open 揪團 */
	@GetMapping("/open")
	public ResponseEntity<List<PickupGames>> getOpenGames() {
		return ResponseEntity.ok(pickupService.findAllOpen());
	}

	/** GET /api/pickup-games/{id} — 查單一揪團 */
	@GetMapping("/{id}")
	public ResponseEntity<PickupGames> getGameById(@PathVariable Integer id) {
		return ResponseEntity.ok(pickupService.findById(id));
	}

	// ===== POST 建立 =====

	/** POST /api/pickup-games — 建立揪團 */
	@PostMapping
	public ResponseEntity<?> createGame(@RequestBody CreatePickupGameRequest req) {
		try {
			PickupGames created = pickupService.createGame(req);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// ===== PUT 更新 =====

	/** PUT /api/pickup-games/{id} — 修改揪團 */
	@PutMapping("/{id}")
	public ResponseEntity<?> updateGame(
			@PathVariable Integer id,
			@RequestBody CreatePickupGameRequest req) {
		try {
			PickupGames updated = pickupService.updateGame(id, req);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	/** PUT /api/pickup-games/{id}/cancel — 取消揪團 */
	@PutMapping("/{id}/cancel")
	public ResponseEntity<?> cancelGame(@PathVariable Integer id) {
		try {
			PickupGames cancelled = pickupService.cancelGame(id);
			return ResponseEntity.ok(cancelled);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
