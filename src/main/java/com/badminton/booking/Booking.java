package com.badminton.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.badminton.court.Court;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "Bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id")
	private Integer bookingId;
	
	//之後建 Member 再改 @ManyToOne
	@Column(name = "member_id")
	private Integer memberId;
	
	@ManyToOne
	@JoinColumn(name = "court_id",nullable = false)
	private Court court;
	
	@Column(name = "booking_date")
	private LocalDate bookingDate;
	
	@Column(name = "start_time")
	private LocalTime startTime;
	
	@Column(name = "end_time")
	private LocalTime endTime;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private BookingStatus status = BookingStatus.CONFIRMED;
	
	@Column(name = "total_amount")
	private BigDecimal totalAmount;
	
	@Column(name = "note")
	private String note;
	
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	
	// 在建構時自動填入當前時間
	@PrePersist    // ← 在 save() 之前自動執行
	protected void onCreate() {
	    this.createdAt = LocalDateTime.now();
	}
	

}
