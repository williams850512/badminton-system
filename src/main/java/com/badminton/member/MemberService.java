package com.badminton.member;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    
    @Autowired
    private MemberRepository memberRepo;

    /**
     * 1. 會員登入 (保留原本 COLLATE 邏輯，並更新最後登入時間)
     */
    @Transactional
    public Optional<MemberBean> login(String username, String password) {
        if (username == null || password == null) return Optional.empty();
        Optional<MemberBean> member = memberRepo.findByLogin(username, password);
        member.ifPresent(m -> memberRepo.updateLastLoginTime(m.getMemberId()));
        return member;
    }

    /**
     * 2. 會員註冊 / 新增會員 (保留 🛡️ 帳號重複檢查安全邏輯)
     */
    @Transactional
    public MemberBean register(MemberBean m) {
        if (m == null || m.getUsername() == null) return null;

        if (isUsernameExists(m.getUsername())) {
            throw new RuntimeException("註冊失敗：帳號 [" + m.getUsername() + "] 已被佔用。");
        }
        
        // 設定初始預設值 (與 SQL DEFAULT 保持一致)
        if (m.getStatus() == null) m.setStatus("Active");
        if (m.getMembershipLevel() == null) m.setMembershipLevel("Normal");

        return memberRepo.save(m);
    }

    /**
     * 檢查會員帳號是否存在 (對應原本 isUsernameExists)
     */
    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        return memberRepo.existsByUsername(username.trim());
    }

    /**
     * 3. 根據 ID 取得單一會員 (對應原本 getMemberById)
     */
    public Optional<MemberBean> getMemberById(int id) {
        return memberRepo.findById(id);
    }

    /**
     * 4. 取得所有會員清單 (對應原本 getAllMembers)
     */
    public List<MemberBean> getAllMembers() {
        return memberRepo.findAll();
    }

    /**
     * 5. 搜尋會員 (對應原本 searchMembers，呼叫 Repository 的 9 欄位搜尋)
     */
    public List<MemberBean> searchMembers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return memberRepo.findAll();
        }
        return memberRepo.searchMembers(keyword.trim());
    }

    /**
     * 6. 會員修改個人資料 (只更新允許的欄位，防止覆蓋密碼等敏感資料)
     */
    @Transactional
    public MemberBean updateMember(MemberBean m) {
        if (m == null) return null;
        return memberRepo.findById(m.getMemberId()).map(existing -> {
            existing.setFullName(m.getFullName());
            existing.setPhone(m.getPhone());
            existing.setEmail(m.getEmail());
            existing.setGender(m.getGender());
            existing.setBirthday(m.getBirthday());
            existing.setProfilePicture(m.getProfilePicture());
            // 不更新 username, password, status, membershipLevel 等敏感欄位
            return memberRepo.save(existing);
        }).orElse(null);
    }

    /**
     * 7. 管理員修改會員資料 (可修改 status, membershipLevel 等敏感欄位)
     */
    @Transactional
    public MemberBean updateMemberByAdmin(MemberBean m) {
        if (m == null) return null;
        return memberRepo.findById(m.getMemberId()).map(existing -> {
            existing.setFullName(m.getFullName());
            existing.setPhone(m.getPhone());
            existing.setEmail(m.getEmail());
            existing.setGender(m.getGender());
            existing.setBirthday(m.getBirthday());
            existing.setProfilePicture(m.getProfilePicture());
            existing.setStatus(m.getStatus());
            existing.setMembershipLevel(m.getMembershipLevel());
            existing.setNote(m.getNote());
            return memberRepo.save(existing);
        }).orElse(null);
    }

    /**
     * 8. 更新會員備註 (保留 updateNote 快速更新功能)
     */
    @Transactional
    public boolean updateNote(int id, String note) {
        int result = memberRepo.updateNote(id, note);
        return result > 0;
    }

    /**
     * 8. 刪除會員 (對應原本 deleteMember)
     */
    @Transactional
    public void deleteMember(int id) {
        memberRepo.deleteById(id);
    }
}
