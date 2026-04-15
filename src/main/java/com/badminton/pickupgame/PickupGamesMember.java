package com.badminton.pickupgame;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【臨打揪團模組 (pickupgame) 的假資料 - 合併時請刪除此檔案】
 * 
 * 這是為了讓 PickupGames / PickupGameSignups 的 @ManyToOne 關聯能正常編譯而建立的暫時 Entity。
 * 此檔案僅供 pickupgame 模組獨立開發測試使用，不是正式的會員 Entity。
 * 
 * TODO: 合併時，請以負責會員功能的同學的 Members Entity 為準，刪除此檔案，
 *       並將 PickupGames / PickupGameSignups 中的 PickupGamesMember 替換成正式的類別。
 */
@Entity @Table(name = "Members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupGamesMember {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Integer memberId;

	@Column(name = "full_name", length = 50)
	private String fullName;
}
