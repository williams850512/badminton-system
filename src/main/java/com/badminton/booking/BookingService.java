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
	
	// ===== 時段重疊判斷（私有工具方法）=====
	
	/**
	 * 判斷兩筆預約的時段是否重疊
	 * 核心公式：A開始 < B結束 且 A結束 > B開始 → 重疊
	 */
	private boolean isOverlapping(Booking a, Booking b) {
		return a.getStartTime().isBefore(b.getEndTime())
			&& a.getEndTime().isAfter(b.getStartTime());
	}
	
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
		// 1. 先撈出「同場地、同日期、未取消」的所有現有預約
		List<Booking> existingBookings = bookingRepo.findByCourtAndBookingDateAndStatusNot(
				booking.getCourt(),        // 同一個場地
				booking.getBookingDate(),  // 同一天
				BookingStatus.CANCELLED);  // 排除已取消的
		
		// 2. 逐筆檢查是否有時段重疊
		for (Booking existing : existingBookings) {
			if (isOverlapping(booking, existing)) {
				throw new RuntimeException("該時段已被預約！");
			}
		}
				
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
