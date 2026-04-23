package com.badminton.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login"; 
    }

    @GetMapping
    public String listPage(HttpSession session) {
        Admin adminUser = (Admin) session.getAttribute("adminUser");
        if (adminUser == null || adminUser.getRole() != AdminRole.MANAGER) {
            return "redirect:/member";
        }
        return "admin/list"; 
    }

    @GetMapping("/add")
    public String addPage() {
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String editPage() {
        return "redirect:/admin";
    }
}
