package com.badminton.court;

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
@RequestMapping("/api/courts")
public class CourtRestController {
	
	@Autowired
	private CourtService courtService;
	
	//Get /api/courts
	@GetMapping
	public List<Court> findAll(){
		return courtService.findAll();
	}
	
	//Get /api/courts/3
	@GetMapping("/{id}")
	public Court findById(@PathVariable Integer id) {
		return courtService.findById(id);
	}
	
	// POST /api/courts  (Body: {"courtName":"聖德基督1號場", ...})
	@PostMapping
	public Court create(@RequestBody Court court) {
		return courtService.save(court);
	}
	
	@PutMapping("/{id}")
	public Court update(@PathVariable Integer id, @RequestBody Court court) {
		court.setCourtId(id);
		return courtService.save(court);
	}
	
	// PATCH /api/courts/3/status  (Body: {"status":"INACTIVE"})
	@PatchMapping("/{id}/status")
	public Court updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
		return courtService.updateStatus(id, CourtStatus.valueOf(body.get("status")));
	}

}