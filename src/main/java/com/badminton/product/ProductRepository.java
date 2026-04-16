package com.badminton.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 商品 Repository — 取代原本的 ProductsDao + ProductDAO
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 依商品名稱精準查詢（僅 active 狀態）
    Product findByProductNameAndStatus(String productName, String status);

    // 模糊搜尋商品（名稱、分類、品牌）
    @Query("SELECT p FROM Product p WHERE " +
           "p.productName LIKE %:keyword% OR " +
           "p.category LIKE %:keyword% OR " +
           "p.brand LIKE %:keyword%")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    // 模糊搜尋（僅 active 狀態，用於前台/訂單查詢）
    @Query("SELECT p FROM Product p WHERE p.status = 'active' AND " +
           "(p.productName LIKE %:keyword% OR " +
           "CAST(p.productId AS string) LIKE %:keyword%)")
    List<Product> searchActiveByKeyword(@Param("keyword") String keyword);

    // 依狀態查詢
    List<Product> findByStatus(String status);
}
