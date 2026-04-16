package com.badminton.pickupgame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupGameRepository extends JpaRepository<PickupGames, Integer> {

}
