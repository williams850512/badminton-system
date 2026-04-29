package com.badminton.pickupgame;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupGameRepository extends JpaRepository<PickupGames, Integer> {

	/** 依日期 → 開始時間 升冪排序 */
	List<PickupGames> findAllByOrderByGameDateAscStartTimeAsc();

}
