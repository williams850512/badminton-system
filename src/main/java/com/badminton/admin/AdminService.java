package com.badminton.admin;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 標記為 Spring Service 組件
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepo;

    /**
     * 管理員登入 (保留原本 COLLATE 強制檢查邏輯，並更新最後登入時間)
     */
    @Transactional
    public Optional<AdminBean> login(String username, String password) {
        if (username == null || password == null) return Optional.empty();
        Optional<AdminBean> admin = adminRepo.findByLogin(username, password);
        admin.ifPresent(a -> adminRepo.updateLastLoginTime(a.getAdminId()));
        return admin;
    }

    /**
     * 檢查管理員帳號是否重複 (對應原本 isAdminExists)
     */
    public boolean isAdminExists(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        return adminRepo.existsByUsername(username.trim());
    }

    /**
     * 取得所有管理員清單 (對應原本 getAllAdmins)
     */
    public List<AdminBean> getAllAdmins() {
        return adminRepo.findAll();
    }

    /**
     * 根據 ID 取得特定管理員 (對應原本 getAdminById)
     */
    public Optional<AdminBean> getAdminById(int id) {
        return adminRepo.findById(id);
    }

    /**
     * 新增管理員 (保留 🛡️ 安全檢查邏輯)
     */
    @Transactional
    public AdminBean addAdmin(AdminBean admin) {
        if (admin == null || admin.getUsername() == null) return null;
        
        if (isAdminExists(admin.getUsername())) {
            throw new RuntimeException("新增失敗：帳號 [" + admin.getUsername() + "] 已存在。");
        }
        
        return adminRepo.save(admin);
    }

    /**
     * 更新管理員資料 (對應原本 updateAdmin)
     */
    @Transactional
    public AdminBean updateAdmin(AdminBean admin) {
        if (admin == null || !adminRepo.existsById(admin.getAdminId())) return null;
        return adminRepo.save(admin);
    }

    /**
     * 更新管理員備註 (對應原本 updateAdminNote)
     */
    @Transactional
    public boolean updateAdminNote(int id, String note) {
        String safeNote = (note == null) ? "" : note;
        return adminRepo.findById(id).map(admin -> {
            admin.setNote(safeNote);
            adminRepo.save(admin);
            return true;
        }).orElse(false);
    }

    /**
     * 搜尋管理員 (對應原本 searchAdmins，使用 Repository 定義的 5 欄位模糊搜尋)
     */
    public List<AdminBean> searchAdmins(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return adminRepo.findAll();
        }
        return adminRepo.searchAdmins(keyword.trim());
    }

    /**
     * 刪除管理員 (對應原本 deleteAdmin)
     */
    @Transactional
    public void deleteAdmin(int id) {
        adminRepo.deleteById(id);
    }
}
