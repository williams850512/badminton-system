package com.badminton.booking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.badminton.court.Court;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
	
	List<Booking> findByCourtAndBookingDateAndStatusNot(Court court, LocalDate date, BookingStatus status);

}
