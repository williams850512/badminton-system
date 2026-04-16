package com.badminton.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * 商品 Service — 業務邏輯層
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

	@Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Integer productId) {
        return productRepository.findById(productId).orElse(null);
    }

    public Product findByNameActive(String productName) {
        return productRepository.findByProductNameAndStatus(productName, "active");
    }

    public List<Product> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.searchByKeyword(keyword.trim());
    }

    public List<Product> searchActiveByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findByStatus("active");
        }
        return productRepository.searchActiveByKeyword(keyword.trim());
    }

    @Transactional
    public Product insert(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Product product) {
        if (product == null || product.getProductId() == null) {
            return null;
        }
        if (!productRepository.existsById(product.getProductId())) {
            return null;
        }
        return productRepository.save(product);
    }

    @Transactional
    public boolean delete(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return false;
        }
        productRepository.deleteById(productId);
        return true;
    }
}
