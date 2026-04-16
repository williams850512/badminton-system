package com.badminton.member;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {

    @GetMapping("/login")
    public String loginPage() {
        return "member/login"; 
    }

    @GetMapping("/register")
    public String registerPage() {
        return "member/register"; 
    }

    @GetMapping
    public String listPage() {
        return "member/list"; 
    }

    @GetMapping("/add")
    public String addPage() {
        return "redirect:/member";
    }

    @GetMapping("/edit")
    public String editPage() {
        return "redirect:/member";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/member/login";
        }
        return "member/profile"; 
    }
}
