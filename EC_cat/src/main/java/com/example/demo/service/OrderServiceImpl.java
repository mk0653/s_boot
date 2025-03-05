package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.annotation.CustomLog;
import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@CustomLog
@RequiredArgsConstructor // ✅ コンストラクタの省略
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    
    @Override
    public List<Order> getAllOrders() {
    	logger.info("注文情報を取得します");
        return orderRepository.findAll();
    }

    @Override
    @Transactional
    public Order createOrder(String customerName, String productName, int quantity) {
        // ✅ `@Builder` を利用して Order を作成
        Order order = Order.builder()
                           .customerName(customerName)
                           .productName(productName)
                           .quantity(quantity)
                           .build();
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        // ✅ `orElseThrow()` で明示的な例外をスロー
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("注文が見つかりません");
        }
        orderRepository.deleteById(id);
    }
}
