package com.badminton.admin;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepo;

    // 管理員登入
    @Transactional
    public Optional<AdminBean> login(String username, String password) {
        if (username == null || password == null) return Optional.empty();
        Optional<AdminBean> admin = adminRepo.findByLogin(username, password);
        admin.ifPresent(a -> adminRepo.updateLastLoginTime(a.getAdminId()));
        return admin;
    }

    // 檢查管理員帳號是否重複 
    public boolean isAdminExists(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        return adminRepo.existsByUsername(username.trim());
    }

    // 取得所有管理員清單
    public List<AdminBean> getAllAdmins() {
        return adminRepo.findAll();
    }

    // 根據 ID 取得特定管理員
    public Optional<AdminBean> getAdminById(int id) {
        return adminRepo.findById(id);
    }

    // 新增管理員
    @Transactional
    public AdminBean addAdmin(AdminBean admin) {
        if (admin == null || admin.getUsername() == null) return null;
        
        if (isAdminExists(admin.getUsername())) {
            throw new RuntimeException("新增失敗：帳號 [" + admin.getUsername() + "] 已存在。");
        }
        
        // 確保非空預設值
        if (admin.getStatus() == null) admin.setStatus("active");
        if (admin.getRole() == null) admin.setRole("staff");
        admin.setFailedAttempts(0);
        
        // 手動寫入時間戳，確保絕不會拋出 NULL 錯誤
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);
        
        return adminRepo.save(admin);
    }

    // 更新管理員資料
    @Transactional
    public AdminBean updateAdmin(AdminBean admin) {
        if (admin == null) return null;
        return adminRepo.findById(admin.getAdminId()).map(existing -> {
            
            // 將前台沒被 disabled 時送來的 fullName 更新進資料庫
            if (admin.getFullName() != null) {
                existing.setFullName(admin.getFullName());
            }
            
            existing.setPhone(admin.getPhone());
            existing.setEmail(admin.getEmail());
            existing.setBirthday(admin.getBirthday());
            
            // 如果是主管編輯自己或其他主管，前端會將這兩個欄位 disabled (不送出 = null)
            // 所以必須加上防呆，只有在非 null 的情況下才更新
            if (admin.getStatus() != null) {
                existing.setStatus(admin.getStatus());
            }
            if (admin.getRole() != null) {
                existing.setRole(admin.getRole());
            }
            
            existing.setNote(admin.getNote());
            return adminRepo.save(existing);
        }).orElse(null);
    }

    // 更新管理員備註
    @Transactional
    public boolean updateAdminNote(int id, String note) {
        String safeNote = (note == null) ? "" : note;
        return adminRepo.findById(id).map(admin -> {
            admin.setNote(safeNote);
            adminRepo.save(admin);
            return true;
        }).orElse(false);
    }

    // 搜尋管理員
    public List<AdminBean> searchAdmins(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return adminRepo.findAll();
        }
        return adminRepo.searchAdmins(keyword.trim());
    }

    // 刪除管理員
    @Transactional
    public void deleteAdmin(int id) {
        adminRepo.deleteById(id);
    }
}
