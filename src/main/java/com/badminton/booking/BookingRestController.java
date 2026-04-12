package com.badminton.booking;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingRestController {
	
	@Autowired
	private BookingService bookingService;
	
	// GET /api/bookings
	@GetMapping
	public List<Booking> findAll(){
		return bookingService.findAll();
	}
	
	// GET /api/bookings/3
	@GetMapping("/{id}")
	public Booking findById(@PathVariable Integer id) {
		return bookingService.findById(id);
				
	}
	
	// POST /api/bookings
	@PostMapping
	public Booking create(@RequestBody Booking booking) {
		return bookingService.save(booking);
	}
	
	// PATCH /api/bookings/3/status  (Body: {"status":"CONFIRMED"})
	@PatchMapping("/{id}/status")
	public Booking updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
		return bookingService.updateStatus(id, BookingStatus.valueOf(body.get("status")));
	}
	
	

}
