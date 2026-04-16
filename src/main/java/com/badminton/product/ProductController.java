package com.badminton.product;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

/**
 * 商品 Controller — 取代原本的 ProductServlet + ProductQueryServlet
 */
@Controller
@RequiredArgsConstructor
public class ProductController {

	@Autowired
    private ProductService productService;

    @Value("${app.upload.dir:#{null}}")
    private String uploadDir;

    @GetMapping("/product")
    public String productIndex() {
        return "product/index"; // 會尋找 src/main/resources/templates/product/index.html
    }

    @GetMapping({"/product/list", "/products"})
    public String productList(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "product/list";
    }

    @GetMapping("/product/search")
    public String searchProduct(@RequestParam(required = false) String keyword, Model model) {
        List<Product> products = productService.searchByKeyword(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "product/list";
    }

    @GetMapping("/product/add")
    public String showInsertForm() {
        return "product/insert";
    }

    @GetMapping("/product/edit")
    public String showUpdateForm(@RequestParam Integer productId, Model model) {
        Product product = productService.findById(productId);
        if (product == null) {
            return "redirect:/product/list";
        }
        model.addAttribute("product", product);
        return "product/update";
    }

    @PostMapping("/product/add")
    public String insertProduct(
            @RequestParam String productName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer stockQty,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String productCreateAt,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        Product product = new Product();
        product.setProductName(productName);
        product.setCategory(category);
        product.setBrand(brand);
        product.setPrice(price);
        product.setStockQty(stockQty);
        product.setDescription(description);
        product.setStatus(status);

        if (productCreateAt != null && !productCreateAt.isEmpty()) {
            try {
                product.setCreatedAt(LocalDate.parse(productCreateAt));
            } catch (Exception e) {
                product.setCreatedAt(LocalDate.now());
            }
        } else {
            product.setCreatedAt(LocalDate.now());
        }

        product.setImageUrl(handleImageUpload(image));

        productService.insert(product);
        redirectAttributes.addFlashAttribute("success", "商品新增成功");
        return "redirect:/product/list";
    }

    @PostMapping("/product/edit")
    public String updateProduct(
            @RequestParam Integer productId,
            @RequestParam String productName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer stockQty,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        Product product = productService.findById(productId);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "找不到商品");
            return "redirect:/product/list";
        }

        product.setProductName(productName);
        product.setCategory(category);
        product.setBrand(brand);
        product.setPrice(price);
        product.setStockQty(stockQty);
        product.setDescription(description);
        product.setStatus(status);

        if (image != null && !image.isEmpty()) {
            product.setImageUrl(handleImageUpload(image));
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            product.setImageUrl(imageUrl);
        }

        productService.update(product);
        redirectAttributes.addFlashAttribute("success", "商品修改成功");
        return "redirect:/product/list";
    }

    @PostMapping("/product/delete")
    public String deleteProduct(
            @RequestParam Integer productId,
            RedirectAttributes redirectAttributes) {

        if (productService.delete(productId)) {
            redirectAttributes.addFlashAttribute("success", "商品刪除成功");
        } else {
            redirectAttributes.addFlashAttribute("error", "刪除失敗");
        }
        return "redirect:/product/list";
    }

    // 取代 ProductQueryServlet API
    @GetMapping("/product/api/query")
    @ResponseBody
    public Object apiQuery(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String keyword) {

        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Product> products = productService.searchActiveByKeyword(keyword.trim());
            if (!products.isEmpty()) {
                return java.util.Map.of("success", true, "products", products);
            }
            return java.util.Map.of("success", false, "message", "找不到符合的商品");
        }

        if (id != null) {
            Product product = productService.findById(id);
            if (product != null && "active".equals(product.getStatus())) {
                return java.util.Map.of("success", true, "product", product);
            }
        }

        if (name != null && !name.trim().isEmpty()) {
            Product product = productService.findByNameActive(name.trim());
            if (product != null) {
                return java.util.Map.of("success", true, "product", product);
            }
        }

        return java.util.Map.of("success", false);
    }

    private String handleImageUpload(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return "images/products/default.png";
        }

        try {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String path = (uploadDir != null) ? uploadDir : System.getProperty("user.dir") + "/src/main/resources/static/images/products";
            
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            image.transferTo(new File(dir, fileName));
            return "images/products/" + fileName;

        } catch (IOException e) {
            e.printStackTrace();
            return "images/products/default.png";
        }
    }
}
