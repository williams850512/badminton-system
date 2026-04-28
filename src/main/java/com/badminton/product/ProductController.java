package com.badminton.product;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 商品頁面 Controller — 只負責回傳 HTML 模板
 * 資料全部透過 AJAX 向 /api/products 拿
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    public String listPage() {
        return "product/list";    // → templates/product/list.html
    }
}
