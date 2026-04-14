package com.badminton.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminPageController {

    @GetMapping("/admin/login")
    public String showAdminLoginPage() {
        return "admin/adminLogin"; 
    }

    @GetMapping("/admin/list")
    public String showAdminListPage() {
        return "admin/adminList"; 
    }

    @GetMapping("/admin/add")
    public String showAdminAddPage() {
        return "admin/adminAdd"; 
    }

    /**
     * 建議維持這個寫法，required = false 能防止 400 錯誤
     * 如果使用者亂入 /admin/edit 網址，會被優雅地導回清單
     */
    @GetMapping("/admin/edit")
    public String showAdminEditPage(@RequestParam(required = false) Integer id) {
        if (id == null) {
            return "redirect:/admin/list";
        }
        return "admin/adminEdit"; 
    }
}
