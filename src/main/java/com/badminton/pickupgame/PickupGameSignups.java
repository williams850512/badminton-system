package com.badminton.pickupgame;

import java.time.LocalDateTime;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class PickupGameSignups {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "signup_id")
	private Integer signupId;

	// 多筆報名對應「同一場」揪團
	@ManyToOne
	@JoinColumn(name = "game_id", nullable = false)
	private PickupGames game;

	// 多筆報名對應「同一個」會員
	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private PickupGamesMember member;

	@Column(nullable = false, length = 10)
	private String status = "joined";

	@Column(name = "signed_up_at", nullable = false)
	private LocalDateTime signedUpAt;

	@PrePersist
	protected void onCreate() {
		if (this.signedUpAt == null) this.signedUpAt = LocalDateTime.now();
	}
}
