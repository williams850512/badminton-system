package com.badminton.booking;

public enum BookingStatus {
	PENDING,      // 待付款（轉帳尚未確認）
	CONFIRMED,    // 已確認
	CANCELLED,    // 已取消
	COMPLETED,    // 已完成
	NO_SHOW       // 未到場
}
