package com.badminton.court;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/courts")
public class CourtController {

	/**
	 * 球場管理頁面（只負責回傳 HTML 模板，資料全部透過 AJAX 向 /api/courts 拿）
	 */
	@GetMapping
	public String listPage() {
		return "court/list";    // → templates/court/list.html
	}

}
