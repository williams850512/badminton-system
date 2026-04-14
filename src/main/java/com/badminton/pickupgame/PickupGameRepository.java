package com.badminton.pickupgame;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupGameRepository extends JpaRepository<PickupGames, Integer> {

	// 查所有 open 狀態的揪團
	List<PickupGames> findByStatus(String status);

	// 檢查同場地同時段是否已有揪團（防止重複開團）
	// 邏輯：同一天、同場地、時間有重疊、狀態是 open
	@Query("SELECT COUNT(g) > 0 FROM PickupGames g " +
	       "WHERE g.court.courtId = :courtId " +
	       "AND g.gameDate = :gameDate " +
	       "AND g.startTime < :endTime " +
	       "AND g.endTime > :startTime " +
	       "AND g.status = 'open'")
	boolean existsCourtTimeConflict(
		@Param("courtId") Integer courtId,
		@Param("gameDate") LocalDate gameDate,
		@Param("startTime") LocalTime startTime,
		@Param("endTime") LocalTime endTime
	);

	// 檢查同一主揪在同時段是否已開團（防止影分身）
	@Query("SELECT COUNT(g) > 0 FROM PickupGames g " +
	       "WHERE g.host.memberId = :hostId " +
	       "AND g.gameDate = :gameDate " +
	       "AND g.startTime < :endTime " +
	       "AND g.endTime > :startTime " +
	       "AND g.status = 'open'")
	boolean existsHostTimeConflict(
		@Param("hostId") Integer hostId,
		@Param("gameDate") LocalDate gameDate,
		@Param("startTime") LocalTime startTime,
		@Param("endTime") LocalTime endTime
	);
}
