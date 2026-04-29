package com.badminton.pickupgame;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupGameSignupsRepository extends JpaRepository<PickupGameSignups, Integer> {
	
	boolean existsByGame_GameIdAndMember_MemberId(Integer gameId, Integer memberId);
	
	List<PickupGameSignups> findByGame_GameId(Integer gameId);

}
