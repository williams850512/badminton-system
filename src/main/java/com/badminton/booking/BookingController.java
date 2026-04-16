package com.badminton.booking;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookings")
public class BookingController {

	/**
	 * 預約管理頁面（只負責回傳 HTML 模板，資料全部透過 AJAX 向 /api/bookings 拿）
	 */
	@GetMapping
	public String listPage() {
		return "booking/list";    // → templates/booking/list.html
	}

}
