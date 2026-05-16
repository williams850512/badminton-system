package com.badminton.pickupgame;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupGameSignupsRepository extends JpaRepository<PickupGameSignups, Integer> {
	
	boolean existsByGame_GameIdAndMember_MemberId(Integer gameId, Integer memberId);
	
	List<PickupGameSignups> findByGame_GameId(Integer gameId);

	/** 計算某場揪團中，指定狀態的報名人數 */
	int countByGame_GameIdAndStatus(Integer gameId, SignupStatus status);

	/** 查詢某會員所有的報名紀錄 */
	List<PickupGameSignups> findByMember_MemberId(Integer memberId);
}
