package com.badminton.announcement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementService {
	
	@Autowired
	private AnnouncementRepository aRepo;
	
	// ===== 查詢 =====
	
	public List<Announcement> findAll(){
		return aRepo.findAll();
	}
	
	public Announcement findById(Integer id) {
		return aRepo.findById(id)
				.orElseThrow(()-> new RuntimeException("找不到公告ID:" + id));
	}
	
	// ===== 新增 / 更新 =====
	
	

}
