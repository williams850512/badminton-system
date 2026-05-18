package com.badminton.booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<?> create(@RequestBody Booking booking) {
		try {
			return ResponseEntity.ok(bookingService.save(booking));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}
	
	// PATCH /api/bookings/3/status  (Body: {"status":"CONFIRMED"})
	@PatchMapping("/{id}/status")
	public Booking updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
		return bookingService.updateStatus(id, BookingStatus.valueOf(body.get("status")));
	}
	
	// GET /api/bookings/search?keyword=總館
	@GetMapping("/search")
	public List<Booking> search(@RequestParam(required = false) String keyword){
		return bookingService.searchByKeyword(keyword);
		
	}
	
	// GET /api/bookings/court/5/date/2026-05-15
	@GetMapping("/court/{courtId}/date/{date}")
	public List<Booking> findByCourtAndDate(
	        @PathVariable Integer courtId,
	        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
	    return bookingService.findByCourtAndDate(courtId, date);
	}

	// GET /api/bookings/my-bookings — 取得自己未來已確認的預約（前台發起揪團用）
	@GetMapping("/my-bookings")
	public org.springframework.http.ResponseEntity<?> getMyBookings(jakarta.servlet.http.HttpServletRequest request) {
		Integer userId = (Integer) request.getAttribute("jwtUserId");
		if (userId == null) {
			return org.springframework.http.ResponseEntity.status(401).body("請先登入");
		}
		return org.springframework.http.ResponseEntity.ok(bookingService.findMyUpcomingBookings(userId));
	}

	// GET /api/bookings/my-all-bookings — 取得自己的所有預約紀錄（會員中心用）
	@GetMapping("/my-all-bookings")
	public org.springframework.http.ResponseEntity<?> getMyAllBookings(jakarta.servlet.http.HttpServletRequest request) {
		Integer userId = (Integer) request.getAttribute("jwtUserId");
		if (userId == null) {
			return org.springframework.http.ResponseEntity.status(401).body("請先登入");
		}
		return org.springframework.http.ResponseEntity.ok(bookingService.findAllByMemberId(userId));
	}

}
