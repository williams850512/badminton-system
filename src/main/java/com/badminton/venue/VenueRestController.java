package com.badminton.venue;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/venues")
public class VenueRestController {
	
	@Autowired
	private VenueService venueService;
	
	// GET /api/venues
	@GetMapping
	public List<Venue> findAll(){
		return venueService.findAll();
	}
	
	// GET /api/venues/3
	@GetMapping("/{id}")
	public Venue findById(@PathVariable Integer id) {
		return venueService.findById(id);
	}
	
	// POST /api/venues  (Body: {"venueName":"大安運動中心", ...})
	@PostMapping
	public Venue create(@RequestBody Venue venue) {
		return venueService.save(venue);
	}
	
	@PutMapping("/{id}")
	public Venue update(@PathVariable Integer id, @RequestBody Venue venue) {
		venue.setVenueId(id);
		return venueService.save(venue);
	}
	
	// PATCH /api/venues/3/status  (Body: {"status":"INACTIVE"})
	@PatchMapping("/{id}/status")
	public Venue updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
		return venueService.updateStatus(id, VenueStatus.valueOf(body.get("status")));
	}

}
