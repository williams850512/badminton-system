package com.badminton.pickupgame;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupGameSignupsRepository extends JpaRepository<PickupGameSignups, Integer> {
	
	boolean existsByGame_GameIdAndMember_MemberId(Integer gameId, Integer memberId);
	
	// 🌟 新增：加上狀態過濾，避免 CANCELLED 的舊紀錄擋住重新報名
	boolean existsByGame_GameIdAndMember_MemberIdAndStatus(Integer gameId, Integer memberId, SignupStatus status);
	
	// 🌟 新增：用於找出可能已經變成 CANCELLED 的舊報名紀錄
	java.util.Optional<PickupGameSignups> findByGame_GameIdAndMember_MemberId(Integer gameId, Integer memberId);
	
	List<PickupGameSignups> findByGame_GameId(Integer gameId);

	/** 計算某場揪團中，指定狀態的報名人數 */
	int countByGame_GameIdAndStatus(Integer gameId, SignupStatus status);

	/** 查詢某會員所有的報名紀錄 */
	List<PickupGameSignups> findByMember_MemberId(Integer memberId);
}