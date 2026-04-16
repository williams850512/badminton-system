package com.badminton.pickupgame;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pickup-games")
public class PickupGameRestController {

	@Autowired
	private PickupGamesService pickupGamesService;

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
}
