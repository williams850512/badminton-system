package com.badminton.venue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

	// DELETE /api/venues/3
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		venueService.deleteById(id);
	}

	// POST /api/venues/upload → 上傳場館圖片（支援刪除舊圖）
	@PostMapping("/upload")
	public ResponseEntity<Map<String, String>> uploadImage(
			@RequestParam("image") MultipartFile image,
			@RequestParam(value = "oldImageUrl", required = false) String oldImageUrl) {
		if (image == null || image.isEmpty()) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "請選擇圖片檔案"));
		}

		try {
			// 產生唯一檔名（避免重複）
			String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

			// 存到外部上傳目錄
			File dir = new File("./uploads/venues").getAbsoluteFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File dest = new File(dir, fileName);
			image.transferTo(dest);

			// 刪除舊圖片（如果有的話）
			if (oldImageUrl != null && oldImageUrl.startsWith("/uploads/venues/")) {
				String oldFileName = oldImageUrl.replace("/uploads/venues/", "");
				File oldFile = new File(dir, oldFileName);
				if (oldFile.exists()) {
					oldFile.delete();
				}
			}

			// 回傳可存取的 URL 路徑
			String imageUrl = "/uploads/venues/" + fileName;
			return ResponseEntity.ok(Map.of("imageUrl", imageUrl));

		} catch (IOException e) {
			return ResponseEntity.internalServerError()
					.body(Map.of("error", "圖片上傳失敗：" + e.getMessage()));
		}
	}

}
