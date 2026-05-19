-- ============================================
-- 公告新增圖片欄位 + 預設圖片資料
-- 執行環境：SQL Server / BadmintonDB
-- ============================================

USE BadmintonDB;
GO

-- 1. 新增 image_url 欄位（允許 NULL，舊公告不需要圖片）
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'Announcements' AND COLUMN_NAME = 'image_url'
)
BEGIN
    ALTER TABLE dbo.Announcements ADD image_url NVARCHAR(500) NULL;
    PRINT N'✅ Announcements.image_url 欄位新增完成';
END
ELSE
BEGIN
    PRINT N'ℹ️ image_url 欄位已存在，跳過';
END
GO

-- 2. 為首頁輪播的 5 筆置頂公告設定預設圖片
--    圖片存放在 src/main/resources/static/images/announcements/（會被 Git 追蹤）
--    Spring Boot 會自動把 /images/** 映射到 static 目錄

-- ID=12 場館 Wi-Fi 服務全面升級（置頂）
UPDATE dbo.Announcements SET image_url = N'/images/announcements/wifi.png' WHERE announcement_id = 12;
-- ID=4  五月份教練課程時間表公佈（置頂）
UPDATE dbo.Announcements SET image_url = N'/images/announcements/coaching.png' WHERE announcement_id = 4;
-- ID=2  夏季羽球聯誼賽（置頂）
UPDATE dbo.Announcements SET image_url = N'/images/announcements/tournament.png' WHERE announcement_id = 2;
-- ID=11 VIP會員專屬福利（置頂）
UPDATE dbo.Announcements SET image_url = N'/images/announcements/vip.png' WHERE announcement_id = 11;
-- ID=1  系統上線公告（置頂）
UPDATE dbo.Announcements SET image_url = N'/images/announcements/system_launch.png' WHERE announcement_id = 1;
-- ID=8  新品到貨通知
UPDATE dbo.Announcements SET image_url = N'/images/announcements/newproduct.png' WHERE announcement_id = 8;

PRINT N'✅ 6 筆公告圖片已設定完成';
GO
