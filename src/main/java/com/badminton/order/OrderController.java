package com.badminton.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 取得所有訂單列表
    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "order/list"; // 對應到 src/main/resources/templates/order/list.html
    }

    // 檢視單筆訂單的詳細內容（包含訂單明細）
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Order order = orderService.getOrderById(id);
        List<OrderItem> items = orderService.getItemsByOrderId(id);
        
        model.addAttribute("order", order);
        model.addAttribute("items", items);
        
        return "order/detail"; 
    }

    // 進入新增訂單畫面
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("order", new Order());
        return "order/add"; 
    }

    // 處理新增訂單的表單送出
    @PostMapping("/add")
    public String add(@ModelAttribute Order order) {
        orderService.saveOrder(order);
        return "redirect:/admin/orders"; // 完成後導向回列表頁
    }
    
    // 刪除訂單
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return "redirect:/admin/orders";
    }
}
