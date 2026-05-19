package com.badminton.order;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    // ========================================
    // JWT 權限檢查工具方法
    // ========================================

    /** 從 JWT 取得角色（MANAGER / STAFF / MEMBER） */
    private String getRole(HttpServletRequest request) {
        return (String) request.getAttribute("jwtRole");
    }

    /** 從 JWT 取得使用者 ID */
    private Integer getUserId(HttpServletRequest request) {
        return (Integer) request.getAttribute("jwtUserId");
    }

    /** 是否為管理員（MANAGER 或 STAFF） */
    private boolean isAdmin(HttpServletRequest request) {
        String role = getRole(request);
        return "MANAGER".equals(role) || "STAFF".equals(role);
    }

    /** 是否為會員 */
    private boolean isMember(HttpServletRequest request) {
        return "MEMBER".equals(getRole(request));
    }

    // ========================================
    // 訂單 CRUD
    // ========================================

    // GET /api/orders          → 全部訂單（後台用，需管理權限）
    // GET /api/orders?memberId=3 → 該會員訂單（前台用，需本人或管理員）
    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) Integer memberId,
                                     HttpServletRequest request) {
        if (memberId != null) {
            // 前台查詢：驗證是否本人或管理員
            if (isAdmin(request)) {
                return ResponseEntity.ok(orderService.getOrdersByMemberId(memberId));
            }
            Integer currentUserId = getUserId(request);
            if (isMember(request) && currentUserId != null && currentUserId.equals(memberId)) {
                return ResponseEntity.ok(orderService.getOrdersByMemberId(memberId));
            }
            return ResponseEntity.status(403).body("無權查看此會員訂單");
        }

        // 無 memberId = 後台列出全部訂單（需管理權限）
        if (isAdmin(request)) {
            return ResponseEntity.ok(orderService.getAllOrders());
        }
        return ResponseEntity.status(403).body("需要管理權限");
    }

    // GET /api/orders/3 → 查詢單筆訂單
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id, HttpServletRequest request) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        if (isAdmin(request)) {
            return ResponseEntity.ok(order);
        }
        Integer currentUserId = getUserId(request);
        if (isMember(request) && order.getMember() != null
                && currentUserId != null && currentUserId.equals(order.getMember().getMemberId())) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.status(403).body("無權查看此訂單");
    }

    // POST /api/orders → 建立訂單 (Body: JSON)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Order order, HttpServletRequest request) {
        if (getUserId(request) == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        // ★ 防禦 IDOR 攻擊：非管理員時，強制將訂單綁定為當前登入會員，防止竄改 memberId
        if (isMember(request)) {
            com.badminton.member.Member member = new com.badminton.member.Member();
            member.setMemberId(getUserId(request));
            order.setMember(member);
        }

        orderService.saveOrder(order);
        return ResponseEntity.ok(order);
    }

    // PUT /api/orders/3 → 更新訂單（可變更 status, paymentType, note，需管理權限）
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Order order,
                                     HttpServletRequest request) {
        Order existing = orderService.getOrderById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // ★ 權限防護：僅管理員可修改訂單
        if (!isAdmin(request)) {
            return ResponseEntity.status(403).body("需要管理權限才能修改訂單");
        }

        orderService.updateOrder(id, order.getStatus(), order.getPaymentType(), order.getNote());
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // PATCH /api/orders/3/status → 變更訂單狀態（需管理權限）
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body,
                                           HttpServletRequest request) {
        Order existing = orderService.getOrderById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // ★ 權限防護：僅管理員可變更訂單狀態
        if (!isAdmin(request)) {
            return ResponseEntity.status(403).body("需要管理權限才能變更訂單狀態");
        }

        OrderStatus newStatus = OrderStatus.valueOf(body.get("status"));
        orderService.updateOrder(id, newStatus, existing.getPaymentType(), existing.getNote());
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // DELETE /api/orders/3 → 刪除訂單（含底下明細，需管理權限）
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, HttpServletRequest request) {
        Order existing = orderService.getOrderById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // ★ 權限防護：僅管理員可刪除訂單
        if (!isAdmin(request)) {
            return ResponseEntity.status(403).body("需要管理權限才能刪除訂單");
        }

        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    // ========================================
    // 訂單明細 CRUD
    // ========================================

    // GET /api/orders/3/items → 取得該訂單所有明細
    @GetMapping("/{orderId}/items")
    public ResponseEntity<?> findItemsByOrderId(@PathVariable Integer orderId, HttpServletRequest request) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        if (isAdmin(request)) {
            return ResponseEntity.ok(orderService.getItemsByOrderId(orderId));
        }
        Integer currentUserId = getUserId(request);
        if (isMember(request) && order.getMember() != null
                && currentUserId != null && currentUserId.equals(order.getMember().getMemberId())) {
            return ResponseEntity.ok(orderService.getItemsByOrderId(orderId));
        }
        return ResponseEntity.status(403).body("無權查看此訂單明細");
    }

    // GET /api/orders/3/items/5 → 取得單筆明細
    @GetMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderItem> findItemById(@PathVariable Integer orderId,
                                                  @PathVariable Integer itemId) {
        OrderItem item = orderService.getOrderItemById(itemId);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }

    // POST /api/orders/3/items → 新增明細（需驗證訂單歸屬）
    @PostMapping("/{orderId}/items")
    public ResponseEntity<?> createItem(@PathVariable Integer orderId, @RequestBody OrderItem item,
                                        HttpServletRequest request) {
        if (getUserId(request) == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        // ★ 防禦越權：驗證該訂單確實屬於當前登入會員，防止跨帳戶注入明細
        if (isMember(request)) {
            Order order = orderService.getOrderById(orderId);
            Integer currentUserId = getUserId(request);
            if (order == null || order.getMember() == null
                    || !currentUserId.equals(order.getMember().getMemberId())) {
                return ResponseEntity.status(403).body("無權為此訂單新增明細");
            }
        }

        item.setOrderId(orderId);
        orderService.saveOrderItem(item);
        return ResponseEntity.ok(item);
    }

    // PUT /api/orders/3/items/5 → 更新明細（需管理權限）
    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable Integer orderId,
                                        @PathVariable Integer itemId,
                                        @RequestBody OrderItem item,
                                        HttpServletRequest request) {
        OrderItem existing = orderService.getOrderItemById(itemId);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // ★ 權限防護：僅管理員可修改明細
        if (!isAdmin(request)) {
            return ResponseEntity.status(403).body("需要管理權限才能修改明細");
        }

        orderService.updateOrderItem(itemId, item.getProduct(), item.getQuantity(), item.getUnitPrice());
        return ResponseEntity.ok(orderService.getOrderItemById(itemId));
    }

    // DELETE /api/orders/3/items/5 → 刪除單筆明細（需管理權限）
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Integer orderId,
                                        @PathVariable Integer itemId,
                                        HttpServletRequest request) {
        OrderItem existing = orderService.getOrderItemById(itemId);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // ★ 權限防護：僅管理員可刪除明細
        if (!isAdmin(request)) {
            return ResponseEntity.status(403).body("需要管理權限才能刪除明細");
        }

        orderService.deleteOrderItem(itemId);
        return ResponseEntity.ok().build();
    }

}
