package com.badminton.pickupgame.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

/**
 * 前端建立揪團時送過來的 JSON 格式
 * 
 * 為什麼需要 DTO？
 * 因為 Entity 裡的 host 和 court 是「物件」(Members, Courts)，
 * 但前端只會送 hostId 和 courtId（整數），
 * 所以用 DTO 接收，再由 Service 轉換成 Entity。
 */
@Data
public class CreatePickupGameRequest {

	private Integer hostId;
	private Integer courtId;
	private LocalDate gameDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private Integer maxPlayers;
	private String skillLevel;
	private BigDecimal feePerPerson;
	private String description;
}
