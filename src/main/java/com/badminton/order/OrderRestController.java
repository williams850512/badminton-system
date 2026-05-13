package com.badminton.order;

import com.badminton.admin.Admin;
import com.badminton.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    // ========================================
    // ?? 權限檢查工具方法
    // ========================================

    /** 從 Session 取得管理員（未登入回傳 null） */
    private Admin getAdmin(HttpSession session) {
        return (session != null) ? (Admin) session.getAttribute("adminUser") : null;
    }

    /** 從 Session 取得會員（未登入回傳 null） */
    private Member getMember(HttpSession session) {
        return (session != null) ? (Member) session.getAttribute("user") : null;
    }

    // TODO: 與 Member 模組 merge 後，將此改為 false 以啟用完整權限驗證
    private static final boolean DEMO_MODE = true;

    // ========================================
    // 訂單 CRUD
    // ========================================

    // GET /api/orders          → 全部訂單（後台用，需管理員權限）
    // GET /api/orders?memberId=3 → 該會員的訂單（前台用，需本人或管理員）
    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) Integer memberId,
                                     HttpSession session) {
        Admin admin = getAdmin(session);
        Member member = getMember(session);

        if (memberId != null) {
            // 前台查詢：驗證是否為本人或管理員
            if (DEMO_MODE || admin != null) {
                return ResponseEntity.ok(orderService.getOrdersByMemberId(memberId));
            }
            if (member != null && member.getMemberId() == memberId) {
                return ResponseEntity.ok(orderService.getOrdersByMemberId(memberId));
            }
            return ResponseEntity.status(401).body("請先登入會員");
        }

        // 無 memberId = 後台列出全部訂單（需管理員）
        if (DEMO_MODE || admin != null) {
            return ResponseEntity.ok(orderService.getAllOrders());
        }
        return ResponseEntity.status(403).body("需要管理員權限");
    }

    // GET /api/orders/3 → 查單一訂單
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id, HttpSession session) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        Admin admin = getAdmin(session);
        Member member = getMember(session);

        if (DEMO_MODE || admin != null) {
            return ResponseEntity.ok(order);
        }
        if (member != null && order.getMember() != null
                && member.getMemberId() == order.getMember().getMemberId()) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.status(403).body("無權限查看此訂單");
    }

    // POST /api/orders → 建立訂單 (Body: JSON)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Order order, HttpSession session) {
        Admin admin = getAdmin(session);
        Member member = getMember(session);

        if (!DEMO_MODE && admin == null && member == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        orderService.saveOrder(order);
        return ResponseEntity.ok(order);
    }

    // PUT /api/orders/3 → 更新訂單 (變更 status, paymentType, note)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Order order) {
        Order existing = orderService.getOrderById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        orderService.updateOrder(id, order.getStatus(), order.getPaymentType(), order.getNote());
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // PATCH /api/orders/3/status → 變更訂單狀態
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Order existing = orderService.getOrderById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        OrderStatus newStatus = OrderStatus.valueOf(body.get("status"));
        orderService.updateOrder(id, newStatus, existing.getPaymentType(), existing.getNote());
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // DELETE /api/orders/3 → 刪除訂單（含底下明細）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Order existing = orderService.getOrderById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    // ========================================
    // 訂單明細 CRUD
    // ========================================

    // GET /api/orders/3/items → 取得該訂單所有明細
    @GetMapping("/{orderId}/items")
    public ResponseEntity<?> findItemsByOrderId(@PathVariable Integer orderId, HttpSession session) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        Admin admin = getAdmin(session);
        Member member = getMember(session);

        if (DEMO_MODE || admin != null) {
            return ResponseEntity.ok(orderService.getItemsByOrderId(orderId));
        }
        if (member != null && order.getMember() != null
                && member.getMemberId() == order.getMember().getMemberId()) {
            return ResponseEntity.ok(orderService.getItemsByOrderId(orderId));
        }
        return ResponseEntity.status(403).body("無權限查看此訂單明細");
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

    // POST /api/orders/3/items → 新增明細
    @PostMapping("/{orderId}/items")
    public ResponseEntity<?> createItem(@PathVariable Integer orderId, @RequestBody OrderItem item,
                                        HttpSession session) {
        Admin admin = getAdmin(session);
        Member member = getMember(session);

        if (!DEMO_MODE && admin == null && member == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        item.setOrderId(orderId);
        orderService.saveOrderItem(item);
        return ResponseEntity.ok(item);
    }

    // PUT /api/orders/3/items/5 → 更新明細
    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderItem> updateItem(@PathVariable Integer orderId,
                                                @PathVariable Integer itemId,
                                                @RequestBody OrderItem item) {
        OrderItem existing = orderService.getOrderItemById(itemId);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        orderService.updateOrderItem(itemId, item.getProduct(), item.getQuantity(), item.getUnitPrice());
        return ResponseEntity.ok(orderService.getOrderItemById(itemId));
    }

    // DELETE /api/orders/3/items/5 → 刪除單筆明細
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer orderId,
                                           @PathVariable Integer itemId) {
        OrderItem existing = orderService.getOrderItemById(itemId);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        orderService.deleteOrderItem(itemId);
        return ResponseEntity.ok().build();
    }

}
