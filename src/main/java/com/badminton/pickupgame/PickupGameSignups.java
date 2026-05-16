package com.badminton.pickupgame;

import java.time.LocalDateTime;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PickupGameSignups",
	uniqueConstraints = {
		@UniqueConstraint(name = "UQ_Game_Member", columnNames = {"game_id", "member_id"})
	}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PickupGameSignups {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "signup_id")
	private Integer signupId;

	// 多筆報名對應「同一場」揪團
	@ManyToOne
	@JoinColumn(name = "game_id", nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private PickupGames game;

	// 多筆報名對應「同一個」會員
	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	@JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
	private Member member;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private SignupStatus status = SignupStatus.JOINED;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "signed_up_at", nullable = false)
	private LocalDateTime signedUpAt;

	@PrePersist
	protected void onCreate() {
		if (this.signedUpAt == null) this.signedUpAt = LocalDateTime.now();
	}
}
