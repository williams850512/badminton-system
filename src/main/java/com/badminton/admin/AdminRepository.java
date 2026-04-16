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

    // 1. login (區分大小寫的 COLLATE 語法)
    @Query(value = "SELECT * FROM Admins WHERE username = :un COLLATE Latin1_General_CS_AS " +
                   "AND password = :pw COLLATE Latin1_General_CS_AS AND status = 'active'", 
           nativeQuery = true)
    Optional<AdminBean> findByLogin(@Param("un") String username, @Param("pw") String password);

    // 2. isAdminExists (帳號重複檢查)
    boolean existsByUsername(String username);

    // 3. searchAdmins (ID、帳號、姓名、電話、信箱 5 欄位模糊搜尋)
    @Query("SELECT a FROM AdminBean a WHERE " +
           "CAST(a.adminId AS string) LIKE %:kw% OR " +
           "a.username LIKE %:kw% OR " +
           "a.fullName LIKE %:kw% OR " +
           "a.phone LIKE %:kw% OR " +
           "a.email LIKE %:kw% " +
           "ORDER BY a.adminId ASC")
    List<AdminBean> searchAdmins(@Param("kw") String keyword);

    // 4. updateLastLoginTime (直接呼叫 SQL Server 的 GETDATE())
    @Modifying
    @Transactional
    @Query(value = "UPDATE Admins SET last_login_at = GETDATE() WHERE admin_id = :id", nativeQuery = true)
    void updateLastLoginTime(@Param("id") int adminId);

    // 5. updateAdminNote (單獨更新備註功能)
    @Modifying
    @Transactional
    @Query("UPDATE AdminBean a SET a.note = :note WHERE a.adminId = :id")
    int updateAdminNote(@Param("id") int id, @Param("note") String note);
}
