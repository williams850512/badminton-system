package com.badminton.venue;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/venues")
public class VenueController {

	/**
	 * 場館管理頁面（只負責回傳 HTML 模板，資料全部透過 AJAX 向 /api/venues 拿）
	 */
	@GetMapping
	public String listPage() {
		return "venue/list";    // → templates/venue/list.html
	}

}
