package com.badminton.announcement;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementRestController {
	
	@Autowired
	private AnnouncementService aService;
	
	// GET /api/announcement
	@GetMapping
	public List<Announcement> findAll(){
		return aService.findAll();
	}
	
	// GET /api/announcement/2 <-{id}
	@GetMapping("/{id}")
	public Announcement findById(@PathVariable Integer id) {
		return aService.findById(id);
		
	}
	
	// POST /api/announcement (Body: {"title":"緊急通知", ...})
	@PostMapping
	public Announcement create(@RequestBody Announcement announcement) {
		return aService.save(announcement);
	}
	
	//PUT /api/announcement/2 <-{id}
	@PutMapping("/{id}")
	public Announcement update(@PathVariable Integer id, @RequestBody Announcement announcement) {
		announcement.setAnnouncementId(id);
		return aService.save(announcement);
	}
	
	@PatchMapping("/{id}/status") //(Body: {"status":"PUBLISHED"})
	public Announcement updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
		return aService.updateStatus(id, AnnouncementStatus.valueOf(body.get("status")));
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		aService.deleteById(id);
		
	}
	

}
