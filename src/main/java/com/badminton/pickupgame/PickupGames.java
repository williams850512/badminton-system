package com.badminton.pickupgame;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.badminton.court.Courts;
import com.badminton.member.Members;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PickupGames")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupGames {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "game_id")
	private Integer gameId;

	// ===== ManyToOne 關聯 =====
	// host_id 是 FK → Members 表的 member_id
	// 多筆 PickupGames 可以對應「同一個」Member（發起人）
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "host_id", nullable = false)
	private Members host;

	// court_id 是 FK → Courts 表的 court_id
	// 多筆 PickupGames 可以在「同一個」Court 上舉行
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "court_id", nullable = false)
	private Courts court;

	@Column(name = "game_date", nullable = false)
	private LocalDate gameDate;

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalTime endTime;

	@Column(name = "max_players", nullable = false)
	private Integer maxPlayers;

	@Column(name = "current_players", nullable = false)
	private Integer currentPlayers;

	@Column(name = "skill_level", nullable = false, length = 15)
	private String skillLevel;

	@Column(name = "fee_per_person", nullable = false)
	private BigDecimal feePerPerson;

	@Column(name = "description", length = 300)
	private String description;

	// 先用 String，之後若要建 Status enum 再改成 enum 型別 + @Enumerated
	@Column(name = "status", nullable = false, length = 10)
	private String status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist // 在 save() 之前自動執行
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		// 設定預設值（對應 DB 的 DEFAULT）
		if (this.currentPlayers == null) this.currentPlayers = 1;
		if (this.skillLevel == null) this.skillLevel = "all";
		if (this.feePerPerson == null) this.feePerPerson = BigDecimal.ZERO;
		if (this.status == null) this.status = "open";
	}
}
