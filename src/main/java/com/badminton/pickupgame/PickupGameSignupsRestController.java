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
@RequestMapping("/api/pickup-game-signups")
public class PickupGameSignupsRestController {

	@Autowired
	private PickupGameSignupsService signupsService;

	// GET /api/pickup-game-signups
	@GetMapping
	public List<PickupGameSignups> findAll() {
		return signupsService.findAll();
	}

	// GET /api/pickup-game-signups/3
	@GetMapping("/{id}")
	public PickupGameSignups findById(@PathVariable Integer id) {
		return signupsService.findById(id);
	}

	// POST /api/pickup-game-signups  (Body: JSON)
	@PostMapping
	public PickupGameSignups create(@RequestBody PickupGameSignups signup) {
		return signupsService.save(signup);
	}

	// PUT /api/pickup-game-signups/3
	@PutMapping("/{id}")
	public PickupGameSignups update(@PathVariable Integer id, @RequestBody PickupGameSignups signup) {
		signup.setSignupId(id);
		return signupsService.save(signup);
	}

	// DELETE /api/pickup-game-signups/3
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		signupsService.deleteById(id);
	}
	
	
}
