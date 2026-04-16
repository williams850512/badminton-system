package com.badminton.announcement;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/announcements")
public class AnnouncementController {
	
	/**
	 * 公告管理頁面（只負責回傳 HTML 模板，資料全部透過 AJAX 向 /api/announcements 拿）
	 */
	@GetMapping
	public String listpage() {
		return "announcement/list";
	}

}
