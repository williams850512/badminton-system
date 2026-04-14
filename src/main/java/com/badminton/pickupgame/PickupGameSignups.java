package com.badminton.pickupgame;

import java.lang.reflect.Member;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class PickupGameSignups {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "signup_id")
	private Integer signupId;
	
	@Column(name = "game_id")
	private Integer gameId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member memberId;
	
	@Column(nullable = false, length = 10)
    private String status = "joined"; // 直接對應資料庫的 DEFAULT 'joined'
	
	@
	private LocalDateTime signedUpAt;
	

}
