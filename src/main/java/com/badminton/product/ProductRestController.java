package com.badminton.product;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品 RESTful API Controller — 回傳 JSON 給前端 AJAX / Vue
 */
@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    // GET /api/products → 取得所有商品
    @GetMapping
    public List<Product> findAll() {
        return productService.findAll();
    }

    // GET /api/products/3 → 取得單筆商品
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    // POST /api/products → 新增商品（Body: JSON）
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.save(product);
    }

    // PUT /api/products/3 → 更新商品
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Integer id, @RequestBody Product product) {
        Product existing = productService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        product.setProductId(id);
        return ResponseEntity.ok(productService.save(product));
    }

    // PATCH /api/products/3/status → 變更商品狀態（Body: {"status":"INACTIVE"}）
    @PatchMapping("/{id}/status")
    public ResponseEntity<Product> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Product existing = productService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
            productService.updateStatus(id, ProductStatus.valueOf(body.get("status")))
        );
    }

    // DELETE /api/products/3 → 刪除商品
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Product existing = productService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        productService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
