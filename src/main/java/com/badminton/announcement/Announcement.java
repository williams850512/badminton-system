package com.badminton.announcement;
import java.time.LocalDateTime;

import com.badminton.admin.Admin;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "Announcements")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "announcement_id")
	private Integer announcementId;
	
	@ManyToOne
	@JoinColumn(name="admin_id")
	@JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
	private Admin admin;
	
	@Column(name = "title", nullable = false, length = 255)
	private String title;
	
	@Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
	private String content;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private AnnouncementStatus status = AnnouncementStatus.DRAFT;
	
	@Column(name = "category")
	private String category;
	
	@Column(name = "is_pinned")
	private Boolean isPinned;
	
	@Column(name = "view_count")
	private Integer viewCount;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	// 新增時自動填入時間
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
	
	// 更新時自動更新修改時間
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

}
