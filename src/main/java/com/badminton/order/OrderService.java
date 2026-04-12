package com.badminton.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return orderRepository.findByMemberId(memberId);
    }

    public List<OrderItem> getItemsByOrderId(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public void saveOrderItem(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }

    public void deleteOrder(Integer id) {
        // 在刪除訂單前，必須先刪除底下的明細，否則會發生資料庫 FK (Foreign Key) 衝突錯誤
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        orderItemRepository.deleteAll(items);
        
        // 明細都刪除乾淨後，再刪除訂單主體
        orderRepository.deleteById(id);
    }
}
