package com.badminton.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminBean, Integer> {

    // ✅ 功能 1：login (還原舊專案：嚴格區分大小寫的 COLLATE 語法)
    @Query(value = "SELECT * FROM Admins WHERE username = :un COLLATE Latin1_General_CS_AS " +
                   "AND password = :pw COLLATE Latin1_General_CS_AS AND status = 'active'", 
           nativeQuery = true)
    Optional<AdminBean> findByLogin(@Param("un") String username, @Param("pw") String password);

    // ✅ 功能 2：isAdminExists (還原舊專案：帳號重複檢查)
    boolean existsByUsername(String username);

    // ✅ 功能 3：searchAdmins (還原舊專案：ID、帳號、姓名、電話、信箱 5 欄位模糊搜尋)
    @Query("SELECT a FROM AdminBean a WHERE " +
           "CAST(a.adminId AS string) LIKE %:kw% OR " +
           "a.username LIKE %:kw% OR " +
           "a.fullName LIKE %:kw% OR " +
           "a.phone LIKE %:kw% OR " +
           "a.email LIKE %:kw% " +
           "ORDER BY a.adminId ASC")
    List<AdminBean> searchAdmins(@Param("kw") String keyword);

    // ✅ 功能 4：updateLastLoginTime (還原舊專案：直接呼叫 SQL Server 的 GETDATE())
    @Modifying
    @Transactional
    @Query(value = "UPDATE Admins SET last_login_at = GETDATE() WHERE admin_id = :id", nativeQuery = true)
    void updateLastLoginTime(@Param("id") int adminId);

    // ✅ 功能 5：updateAdminNote (還原舊專案：單獨更新備註功能)
    @Modifying
    @Transactional
    @Query("UPDATE AdminBean a SET a.note = :note WHERE a.adminId = :id")
    int updateAdminNote(@Param("id") int id, @Param("note") String note);

    /* 💡 舊專案其餘功能已由 JpaRepository 自動提供，不需手動寫 SQL：
       - getAllAdmins() -> 使用 findAll()
       - getAdminById() -> 使用 findById(id)
       - addAdmin()     -> 使用 save(bean)
       - updateAdmin()  -> 使用 save(bean) 
       - deleteAdmin()  -> 使用 deleteById(id)
    */
}
