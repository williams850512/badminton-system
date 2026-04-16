package com.badminton.pickupgame;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pickup-games")
public class PickupGameController {

	/**
	 * 臨打揪團管理頁面（只負責回傳 HTML 模板，資料全部透過 AJAX 向 /api/pickup-games 拿）
	 */
	@GetMapping
	public String listPage() {
		return "pickupgame/list";    // → templates/pickupgame/list.html
	}

}
