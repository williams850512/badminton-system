-- ============================================================
-- 修正所有資料表的 enum 欄位值，統一改為大寫
-- 以配合 Java @Enumerated(EnumType.STRING) 的需求
-- ============================================================

-- === Admins 表 ===
UPDATE Admins SET status = UPPER(status) WHERE status != UPPER(status) COLLATE Latin1_General_CS_AS;
UPDATE Admins SET role = UPPER(role) WHERE role != UPPER(role) COLLATE Latin1_General_CS_AS;

-- === Members 表 ===
UPDATE Members SET status = UPPER(status) WHERE status != UPPER(status) COLLATE Latin1_General_CS_AS;
UPDATE Members SET membership_level = UPPER(membership_level) WHERE membership_level != UPPER(membership_level) COLLATE Latin1_General_CS_AS;

-- === Venues 表 ===
UPDATE Venues SET status = UPPER(status) WHERE status != UPPER(status) COLLATE Latin1_General_CS_AS;

-- === Courts 表 ===
UPDATE Courts SET status = UPPER(status) WHERE status != UPPER(status) COLLATE Latin1_General_CS_AS;

-- === Bookings 表 ===
UPDATE Bookings SET status = UPPER(status) WHERE status != UPPER(status) COLLATE Latin1_General_CS_AS;

-- === Orders 表 ===
UPDATE Orders SET status = UPPER(status) WHERE status != UPPER(status) COLLATE Latin1_General_CS_AS;

-- === Products 表 ===
UPDATE Products SET status = UPPER(status) WHERE status != UPPER(status) COLLATE Latin1_General_CS_AS;

-- === Announcements 表 ===
UPDATE Announcements SET status = UPPER(status) WHERE status != UPPER(status) COLLATE Latin1_General_CS_AS;

-- ============================================================
-- 驗證：查看各表的 enum 欄位現在的值
-- ============================================================
SELECT 'Admins' AS [Table], status, role, COUNT(*) AS cnt FROM Admins GROUP BY status, role;
SELECT 'Members' AS [Table], status, membership_level, COUNT(*) AS cnt FROM Members GROUP BY status, membership_level;
SELECT 'Venues' AS [Table], status, COUNT(*) AS cnt FROM Venues GROUP BY status;
SELECT 'Courts' AS [Table], status, COUNT(*) AS cnt FROM Courts GROUP BY status;
SELECT 'Bookings' AS [Table], status, COUNT(*) AS cnt FROM Bookings GROUP BY status;
SELECT 'Orders' AS [Table], status, COUNT(*) AS cnt FROM Orders GROUP BY status;
SELECT 'Products' AS [Table], status, COUNT(*) AS cnt FROM Products GROUP BY status;
SELECT 'Announcements' AS [Table], status, COUNT(*) AS cnt FROM Announcements GROUP BY status;
