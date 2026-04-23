package com.badminton.member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

	@Autowired
	private MemberRepository memberRepo;

	// 1. 會員登入
	@Transactional
	public Optional<Member> login(String username, String password) {
		if (username == null || password == null)
			return Optional.empty();
		Optional<Member> member = memberRepo.findByLogin(username, password);
		member.ifPresent(m -> memberRepo.updateLastLoginTime(m.getMemberId()));
		return member;
	}

	// 2. 會員註冊
	@Transactional
	public Member register(Member m) {
		if (m == null || m.getUsername() == null)
			return null;

		if (isUsernameExists(m.getUsername())) {
			throw new RuntimeException("註冊失敗：帳號 [" + m.getUsername() + "] 已被佔用。");
		}

		// 設定初始預設值 (與 SQL DEFAULT 保持一致)
		if (m.getStatus() == null)
			m.setStatus(MemberStatus.ACTIVE);
		if (m.getMembershipLevel() == null)
			m.setMembershipLevel(MembershipLevel.NORMAL);

		// 手動寫入時間戳，確保不會觸發 created_at 不能為 NULL 的例外狀況
		LocalDateTime now = LocalDateTime.now();
		m.setCreatedAt(now);
		m.setUpdatedAt(now);

		return memberRepo.save(m);
	}

	// 檢查會員帳號是否存在
	public boolean isUsernameExists(String username) {
		if (username == null || username.trim().isEmpty())
			return false;
		return memberRepo.existsByUsername(username.trim());
	}

	// 3. 根據 ID 取得單一會員
	public Optional<Member> getMemberById(int id) {
		return memberRepo.findById(id);
	}

	// 4. 取得所有會員清單
	public List<Member> getAllMembers() {
		return memberRepo.findAll();
	}

	// 5. 搜尋會員 (對應原本 searchMembers，呼叫 Repository 的 9 欄位搜尋)
	public List<Member> searchMembers(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return memberRepo.findAll();
		}
		return memberRepo.searchMembers(keyword.trim());
	}

	// 6. 會員修改個人資料 (只更新允許的欄位，防止覆蓋密碼等敏感資料)
	@Transactional
	public Member updateMember(Member m) {
		if (m == null)
			return null;
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

	// 7. 管理員修改會員資料 (可修改 status, membershipLevel 等敏感欄位)
	@Transactional
	public Member updateMemberByAdmin(Member m) {
		if (m == null)
			return null;
		return memberRepo.findById(m.getMemberId()).map(existing -> {

			// 允許主管修改姓名，但如果是職員編輯此欄位會被 disabled 並傳回 null，這裡加上防呆
			if (m.getFullName() != null) {
				existing.setFullName(m.getFullName());
			}

			existing.setPhone(m.getPhone());
			existing.setEmail(m.getEmail());
			existing.setBirthday(m.getBirthday());
			existing.setProfilePicture(m.getProfilePicture());

			// 加入防呆檢查：因為如果是由一般職員編輯，前端會鎖定這兩個欄位(傳入 null)
			if (m.getStatus() != null) {
				existing.setStatus(m.getStatus());
			}
			if (m.getMembershipLevel() != null) {
				existing.setMembershipLevel(m.getMembershipLevel());
			}

			existing.setNote(m.getNote());
			return memberRepo.save(existing);
		}).orElse(null);
	}

	// 8. 更新會員備註 (保留 updateNote 快速更新功能)
	@Transactional
	public boolean updateNote(int id, String note) {
		int result = memberRepo.updateNote(id, note);
		return result > 0;
	}

	// 9. 刪除會員 (對應原本 deleteMember)
	@Transactional
	public void deleteMember(int id) {
		memberRepo.deleteById(id);
	}
}
