package com.badminton.pickupgame;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.badminton.court.Court;
import com.badminton.member.Member;
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

@Entity @Table(name = "PickupGames")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupGames {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "game_id")
	private Integer gameId;

	// 多筆揪團可以對應「同一個」主揪（Members）
	@ManyToOne
	@JoinColumn(name = "host_id", nullable = false)
	private Member host;

	// 多筆揪團可以在「同一個」場地（Court）上舉行
	@ManyToOne
	@JoinColumn(name = "court_id", nullable = false)
	private Court court;

	@Column(name = "game_date", nullable = false)
	private LocalDate gameDate;

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalTime endTime;

	@Column(name = "max_players", nullable = false)
	private Integer maxPlayers = 4;

	@Column(name = "current_players", nullable = false)
	private Integer currentPlayers = 1;

	@Enumerated(EnumType.STRING)
	@Column(name = "skill_level", nullable = false, length = 15)
	private SkillLevel skillLevel = SkillLevel.ALL;

	@Column(name = "fee_per_person", nullable = false)
	private BigDecimal feePerPerson = BigDecimal.ZERO;

	@Column(name = "description", length = 300)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 10)
	private PickupGameStatus status = PickupGameStatus.OPEN;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		if (this.createdAt == null) this.createdAt = LocalDateTime.now();
	}
}
