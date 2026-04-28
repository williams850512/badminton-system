package com.badminton.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品 Service — 業務邏輯層
 */
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // ===== 查詢 =====

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    // ===== 新增 / 更新 =====

    public Product save(Product product) {
        return productRepository.save(product);
    }

    // ===== 狀態更新 =====

    public Product updateStatus(Integer id, ProductStatus newStatus) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到商品 ID: " + id));
        product.setStatus(newStatus);
        return productRepository.save(product);
    }

    // ===== 刪除 =====

    public void deleteById(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("找不到商品 ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
