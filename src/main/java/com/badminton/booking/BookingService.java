package com.badminton.booking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookingService {
	
	@Autowired
	private BookingRepository bookingRepo;
	
	// ===== 查詢 =====
	
	public List<Booking> findAll(){
		return bookingRepo.findAll();	
	}
	
	public Booking findById(Integer id) {
		return bookingRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到預約 ID: " + id));
	}
	
	// ===== 新增 / (客戶預約不能更新!) =====
	
	public Booking save(Booking booking) {
		return bookingRepo.save(booking);
	}
	
	// ===== 狀態更新 =====
	
	public Booking updateStatus(Integer id, BookingStatus newStatus) {
		// 1. 先從資料庫撈出這筆 Booking
		Booking booking = bookingRepo.findById(id)
				.orElseThrow(()-> new RuntimeException("找不到預約 ID:" + id));
		// 2. 只修改 status 欄位
		booking.setStatus(newStatus);
		// 3. 存回資料庫（Hibernate 會自動產生 UPDATE SQL）
		return bookingRepo.save(booking);
	}

}
