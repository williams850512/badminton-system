package com.badminton.venue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "Venues")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "venue_id")
	private Integer venueId;
	
	@Column(name = "venue_name", nullable = false, length = 50)
	private String venueName;
	
	@Column(name = "address", length = 200)
	private String address;
	
	@Column(name = "phone", length = 20)
	private String phone;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private VenueStatus status = VenueStatus.ACTIVE;

}
