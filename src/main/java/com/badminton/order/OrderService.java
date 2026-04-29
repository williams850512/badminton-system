package com.badminton.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.badminton.product.Product;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }
    
    public List<Order> getOrdersByMemberId(Integer memberId) {
        return orderRepository.findByMember_MemberId(memberId);
    }

    public List<OrderItem> getItemsByOrderId(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public void saveOrderItem(OrderItem orderItem) {
        // subtotal 自動計算 = quantity × unitPrice (對應 V2 的 OrderItemDAO 邏輯)
        orderItem.setSubtotal(orderItem.getQuantity() * orderItem.getUnitPrice());
        orderItemRepository.save(orderItem);
        // 新增明細後，重新計算訂單總金額
        recalcOrderTotal(orderItem.getOrderId());
    }

    public OrderItem getOrderItemById(Integer itemId) {
        return orderItemRepository.findById(itemId).orElse(null);
    }

    // 更新單筆明細 (對應 V2 的 OrderItemDAO.updateItem)
    public void updateOrderItem(Integer itemId, Product product, Integer quantity, Integer unitPrice) {
        OrderItem item = orderItemRepository.findById(itemId).orElse(null);
        if (item != null) {
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(unitPrice);
            item.setSubtotal(quantity * unitPrice); // subtotal 由後端自動計算
            orderItemRepository.save(item);
            // 更新明細後，重新計算訂單總金額
            recalcOrderTotal(item.getOrderId());
        }
    }

    // 刪除單筆明細 (對應 V2 的 OrderItemDAO.deleteByItemId)
    public void deleteOrderItem(Integer itemId) {
        OrderItem item = orderItemRepository.findById(itemId).orElse(null);
        if (item != null) {
            Integer orderId = item.getOrderId();
            orderItemRepository.deleteById(itemId);
            // 刪除明細後，重新計算訂單總金額
            recalcOrderTotal(orderId);
        }
    }

    // 重新計算訂單總金額 (對應 V2 的 OrderItemActionServlet.recalcOrderTotal)
    private void recalcOrderTotal(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
            int total = items.stream()
                    .mapToInt(i -> i.getQuantity() * i.getUnitPrice())
                    .sum();
            order.setTotalAmount(total);
            orderRepository.save(order);
        }
    }

    // 更新訂單 (對應原版 V2 的 OrderDAO.updateOrder 方法)
    // 只更新 status, paymentType, note 三個欄位
    public void updateOrder(Integer id, OrderStatus status, PaymentType paymentType, String note) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            order.setPaymentType(paymentType);
            order.setNote(note);
            orderRepository.save(order); // JPA 的 save() 偵測到已有 ID，會自動執行 UPDATE
        }
    }

    public void deleteOrder(Integer id) {
        // 在刪除訂單前，必須先刪除底下的明細，否則會發生資料庫 FK (Foreign Key) 衝突錯誤
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        orderItemRepository.deleteAll(items);
        
        // 明細都刪除乾淨後，再刪除訂單主體
        orderRepository.deleteById(id);
    }
}
