package com.badminton.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MemberPageController {

    /**
     * 顯示會員清單主頁面
     */
    @GetMapping("/member/list")
    public String showMemberListPage() {
        return "member/memberList"; 
    }

    /**
     * 顯示新增會員頁面
     */
    @GetMapping("/member/add")
    public String showAddMemberPage() {
        return "member/addMember"; 
    }
    
    /**
     * 顯示編輯會員頁面
     * 建議加上 @RequestParam 並檢查 id，確保編輯時有指定的對象
     */
    @GetMapping("/member/edit")
    public String showEditMemberPage(@RequestParam(name = "id", required = false) Integer id) {
        // 如果網址沒帶 id (例如直接輸入 /member/edit)，就導回清單頁，防止頁面出錯
        if (id == null) {
            return "redirect:/member/list";
        }
        return "member/editMember"; 
    }
    
    @GetMapping("/member/login")
    public String showMemberLoginPage() {
        // 指向 templates/member/memberLogin.html
        return "member/memberLogin"; 
    }
    
    @GetMapping("/member/profile")
    public String showMemberProfilePage() {
        // 指向 templates/member/profile.html
        return "member/profile"; 
    }
    
    @GetMapping("/member/register")
    public String showRegisterPage() {
        return "member/register"; 
    }
}
