package com.badminton.pickupgame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupGameSignupsRepository extends JpaRepository<PickupGameSignups, Integer> {
	boolean existsByGame_GameIdAndMember_MemberId(Integer gameId, Integer memberId);

}
