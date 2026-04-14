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

    // 進入修改訂單畫面 (對應原版 V2 的 OrderUpdateServlet - 顯示表單)
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/admin/orders"; // 找不到就跳回列表
        }
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values()); // 把所有 Enum 傳給前端下拉選單
        return "order/edit"; // 對應到 src/main/resources/templates/order/edit.html
    }

    // 處理修改訂單的表單送出 (對應原版 V2 的 OrderUpdateServlet.doPost)
    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @RequestParam OrderStatus status,
                         @RequestParam String paymentType,
                         @RequestParam String note) {
        orderService.updateOrder(id, status, paymentType, note);
        return "redirect:/admin/orders"; // 完成後導向回列表頁 (同 V2 的 sendRedirect)
    }
    
    // 刪除訂單
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return "redirect:/admin/orders";
    }

    // =====================================================================
    // === 以下為 OrderItem (訂單明細) 的 CRUD 路由 ===
    // === 對應原版 V2 的 OrderItemActionServlet + OrderItemUpdateServlet ===
    // =====================================================================

    // 進入新增明細畫面
    @GetMapping("/{orderId}/items/add")
    public String addItemForm(@PathVariable Integer orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/admin/orders";
        }
        OrderItem newItem = new OrderItem();
        newItem.setOrderId(orderId);
        model.addAttribute("order", order);
        model.addAttribute("item", newItem);
        return "order/item-add";
    }

    // 處理新增明細的表單送出
    @PostMapping("/{orderId}/items/add")
    public String addItem(@PathVariable Integer orderId, @ModelAttribute OrderItem item) {
        item.setOrderId(orderId);
        orderService.saveOrderItem(item);
        return "redirect:/admin/orders/detail/" + orderId;
    }

    // 進入修改明細畫面
    @GetMapping("/{orderId}/items/edit/{itemId}")
    public String editItemForm(@PathVariable Integer orderId,
                               @PathVariable Integer itemId, Model model) {
        Order order = orderService.getOrderById(orderId);
        OrderItem item = orderService.getOrderItemById(itemId);
        if (order == null || item == null) {
            return "redirect:/admin/orders/detail/" + orderId;
        }
        model.addAttribute("order", order);
        model.addAttribute("item", item);
        return "order/item-edit";
    }

    // 處理修改明細的表單送出 (對應 V2 的 OrderItemDAO.updateItem)
    @PostMapping("/{orderId}/items/update/{itemId}")
    public String updateItem(@PathVariable Integer orderId,
                             @PathVariable Integer itemId,
                             @RequestParam Integer productId,
                             @RequestParam Integer quantity,
                             @RequestParam Integer unitPrice) {
        orderService.updateOrderItem(itemId, productId, quantity, unitPrice);
        return "redirect:/admin/orders/detail/" + orderId;
    }

    // 刪除單筆明細 (對應 V2 的 OrderItemDAO.deleteByItemId)
    @PostMapping("/{orderId}/items/delete/{itemId}")
    public String deleteItem(@PathVariable Integer orderId,
                             @PathVariable Integer itemId) {
        orderService.deleteOrderItem(itemId);
        return "redirect:/admin/orders/detail/" + orderId;
    }
}
