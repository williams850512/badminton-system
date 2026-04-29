package com.badminton.booking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.badminton.court.Court;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
	
	List<Booking> findByCourtAndBookingDateAndStatusNot(Court court, LocalDate date, BookingStatus status);
	
	// 跨表模糊搜尋：搜尋場館名稱 OR 球場名稱 OR 會員名稱
	@Query("SELECT b FROM Booking b " +
	       "LEFT JOIN b.court c " +
		   "LEFT JOIN c.venue v " +
	       "LEFT JOIN b.member m " +
		   "WHERE v.venueName LIKE CONCAT('%', :keyword, '%') " +
		   "OR c.courtName LIKE CONCAT('%', :keyword, '%') " +
		   "OR m.fullName LIKE CONCAT('%', :keyword, '%')")
	List<Booking> searchByKeyword(@Param("keyword") String keyword);
	
	List<Booking> findAllByOrderByBookingDateAscStartTimeAsc();

}
