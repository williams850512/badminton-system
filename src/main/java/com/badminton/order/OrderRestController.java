package com.badminton.order;

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

    // =====================================================================
    // === Order (訂單) CRUD ===
    // =====================================================================

    // GET /api/orders → 取得所有訂單
    @GetMapping
    public List<Order> findAll() {
        return orderService.getAllOrders();
    }

    // GET /api/orders/3 → 取得單筆訂單
    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Integer id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    // POST /api/orders → 新增訂單 (Body: JSON)
    @PostMapping
    public Order create(@RequestBody Order order) {
        orderService.saveOrder(order);
        return order;
    }

    // PUT /api/orders/3 → 更新訂單 (只更新 status, paymentType, note)
    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Integer id, @RequestBody Order order) {
        Order existing = orderService.getOrderById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        orderService.updateOrder(id, order.getStatus(), order.getPaymentType(), order.getNote());
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // PATCH /api/orders/3/status → 變更訂單狀態
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
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

    // =====================================================================
    // === OrderItem (訂單明細) CRUD ===
    // =====================================================================

    // GET /api/orders/3/items → 取得某訂單的所有明細
    @GetMapping("/{orderId}/items")
    public List<OrderItem> findItemsByOrderId(@PathVariable Integer orderId) {
        return orderService.getItemsByOrderId(orderId);
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
    public OrderItem createItem(@PathVariable Integer orderId, @RequestBody OrderItem item) {
        item.setOrderId(orderId);
        orderService.saveOrderItem(item);
        return item;
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
