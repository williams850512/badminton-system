package com.badminton.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberBean, Integer> {

    // 1. 登入：還原 COLLATE 嚴格校對邏輯
    @Query(value = "SELECT * FROM Members WHERE " +
                   "username = :un COLLATE Latin1_General_CS_AS AND " +
                   "password = :pw COLLATE Latin1_General_CS_AS", 
           nativeQuery = true)
    Optional<MemberBean> findByLogin(@Param("un") String username, @Param("pw") String password);

    // 2. 查重：還原 isUsernameExists
    boolean existsByUsername(String username);

    // 3. 登入時間：還原資料庫自動取時邏輯
    @Modifying
    @Transactional
    @Query(value = "UPDATE Members SET last_login_at = GETDATE() WHERE member_id = :id", nativeQuery = true)
    void updateLastLoginTime(@Param("id") int memberId);

    // 4. 搜尋：還原 9 個欄位的模糊搜尋與排序邏輯
    @Query("SELECT m FROM MemberBean m WHERE " +
           "CAST(m.memberId AS string) LIKE %:kw% OR " +
           "m.username LIKE %:kw% OR " +
           "m.fullName LIKE %:kw% OR " +
           "m.phone LIKE %:kw% OR " +
           "m.email LIKE %:kw% OR " +
           "CAST(m.birthday AS string) LIKE %:kw% OR " +
           "m.status LIKE %:kw% OR " +
           "m.membershipLevel LIKE %:kw% OR " +
           "m.note LIKE %:kw% " +
           "ORDER BY m.memberId ASC")
    List<MemberBean> searchMembers(@Param("kw") String keyword);

    // 5. 備註更新：還原 updateNote 與 updatedAt 同步更新邏輯
    @Modifying
    @Transactional
    @Query(value = "UPDATE Members SET note = :note, updated_at = GETDATE() WHERE member_id = :id", nativeQuery = true)
    int updateNote(@Param("id") int memberId, @Param("note") String note);

    // 💡 提示：
    // getAllMembers -> 直接呼叫 .findAll()
    // getMemberById -> 直接呼叫 .findById(id)
    // deleteMember  -> 直接呼叫 .deleteById(id)
    // register/update -> 直接呼叫 .save(member)
}
