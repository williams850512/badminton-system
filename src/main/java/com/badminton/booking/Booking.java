package com.badminton.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.badminton.court.Court;
import com.badminton.member.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id")
	private Integer bookingId;
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	@JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
	private Member member;
	
	@ManyToOne
	@JoinColumn(name = "court_id",nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Court court;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(name = "booking_date")
	private LocalDate bookingDate;
	
	@JsonFormat(pattern = "HH:mm")
	@Column(name = "start_time")
	private LocalTime startTime;
	
	@JsonFormat(pattern = "HH:mm")
	@Column(name = "end_time")
	private LocalTime endTime;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private BookingStatus status = BookingStatus.CONFIRMED;
	
	@Column(name = "total_amount")
	private BigDecimal totalAmount;
	
	@Column(name = "note")
	private String note;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	
	// 在建構時自動填入當前時間
	@PrePersist    // ← 在 save() 之前自動執行
	protected void onCreate() {
	    this.createdAt = LocalDateTime.now();
	}
	

}
