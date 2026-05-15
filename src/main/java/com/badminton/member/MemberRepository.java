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
public interface MemberRepository extends JpaRepository<Member, Integer> {

    // 1. 登入
    @Query(value = "SELECT * FROM Members WHERE " +
                   "username = :un COLLATE Latin1_General_CS_AS AND " +
                   "password = :pw COLLATE Latin1_General_CS_AS", 
           nativeQuery = true)
    Optional<Member> findByLogin(@Param("un") String username, @Param("pw") String password);

    // 2. 查重
    boolean existsByUsername(String username);

    // Google 第三方登入相關
    Optional<Member> findByGoogleId(String googleId);
    Optional<Member> findByEmail(String email);

    // 忘記密碼 - 帳號+Email 查詢
    Optional<Member> findByUsernameAndEmail(String username, String email);

    // 3. 登入時間
    @Modifying
    @Transactional
    @Query(value = "UPDATE Members SET last_login_at = GETDATE() WHERE member_id = :id", nativeQuery = true)
    void updateLastLoginTime(@Param("id") int memberId);

    // 4. 搜尋
    @Query("SELECT m FROM Member m WHERE " +
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
    List<Member> searchMembers(@Param("kw") String keyword);

    // 5. 備註更新
    @Modifying
    @Transactional
    @Query(value = "UPDATE Members SET note = :note, updated_at = GETDATE() WHERE member_id = :id", nativeQuery = true)
    int updateNote(@Param("id") int memberId, @Param("note") String note);

    // getAllMembers -> 直接呼叫 .findAll()
    // getMemberById -> 直接呼叫 .findById(id)
    // deleteMember  -> 直接呼叫 .deleteById(id)
    // register/update -> 直接呼叫 .save(member)

    // 6. 忘記密碼 — 驗證身份（帳號 + Email + 生日）
    @Query(value = "SELECT * FROM Members WHERE username = :un AND email = :email AND birthday = :birthday",
           nativeQuery = true)
    Optional<Member> findByUsernameAndEmailAndBirthday(
            @Param("un") String username,
            @Param("email") String email,
            @Param("birthday") String birthday);

    // 7. 重設密碼
    @Modifying
    @Transactional
    @Query(value = "UPDATE Members SET password = :pwd, updated_at = GETDATE() WHERE member_id = :id",
           nativeQuery = true)
    int updatePassword(@Param("id") int memberId, @Param("pwd") String newPassword);

    // ========== Dashboard 統計查詢 ==========

    // 8. 統計今日新增會員數
    @Query(value = "SELECT COUNT(*) FROM Members WHERE CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
    int countTodayNewMembers();

    // 9. 統計本週新增會員數
    @Query(value = "SELECT COUNT(*) FROM Members WHERE DATEDIFF(DAY, created_at, GETDATE()) < 7", nativeQuery = true)
    int countWeekNewMembers();

    // 10. 統計本月新增會員數
    @Query(value = "SELECT COUNT(*) FROM Members WHERE YEAR(created_at) = YEAR(GETDATE()) AND MONTH(created_at) = MONTH(GETDATE())", nativeQuery = true)
    int countMonthNewMembers();

    // 11. 男女比例統計
    @Query(value = "SELECT gender, COUNT(*) AS cnt FROM Members WHERE gender IS NOT NULL GROUP BY gender", nativeQuery = true)
    List<Object[]> countByGender();

    // 12. 年齡分佈統計
    @Query(value = "SELECT " +
            "CASE " +
            "  WHEN DATEDIFF(YEAR, birthday, GETDATE()) <= 20 THEN '20歲以下' " +
            "  WHEN DATEDIFF(YEAR, birthday, GETDATE()) BETWEEN 21 AND 30 THEN '21-30歲' " +
            "  WHEN DATEDIFF(YEAR, birthday, GETDATE()) BETWEEN 31 AND 40 THEN '31-40歲' " +
            "  WHEN DATEDIFF(YEAR, birthday, GETDATE()) BETWEEN 41 AND 50 THEN '41-50歲' " +
            "  ELSE '51歲以上' " +
            "END AS ageGroup, COUNT(*) AS cnt " +
            "FROM Members WHERE birthday IS NOT NULL " +
            "GROUP BY " +
            "CASE " +
            "  WHEN DATEDIFF(YEAR, birthday, GETDATE()) <= 20 THEN '20歲以下' " +
            "  WHEN DATEDIFF(YEAR, birthday, GETDATE()) BETWEEN 21 AND 30 THEN '21-30歲' " +
            "  WHEN DATEDIFF(YEAR, birthday, GETDATE()) BETWEEN 31 AND 40 THEN '31-40歲' " +
            "  WHEN DATEDIFF(YEAR, birthday, GETDATE()) BETWEEN 41 AND 50 THEN '41-50歲' " +
            "  ELSE '51歲以上' " +
            "END " +
            "ORDER BY ageGroup", nativeQuery = true)
    List<Object[]> getAgeDistribution();

    // 13. 近 12 個月會員成長趨勢
    @Query(value = "SELECT FORMAT(created_at, 'yyyy-MM') AS month, COUNT(*) AS cnt " +
            "FROM Members " +
            "WHERE created_at >= DATEADD(MONTH, -11, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)) " +
            "GROUP BY FORMAT(created_at, 'yyyy-MM') " +
            "ORDER BY month", nativeQuery = true)
    List<Object[]> getMonthlyGrowth();

    // 14. 會員等級分佈
    @Query(value = "SELECT membership_level, COUNT(*) AS cnt FROM Members GROUP BY membership_level", nativeQuery = true)
    List<Object[]> countByMembershipLevel();
}
