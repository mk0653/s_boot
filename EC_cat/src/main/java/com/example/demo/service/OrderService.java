package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Order;

public interface OrderService {
    List<Order> getAllOrders();
    Order createOrder(String customerName, String productName, int quantity);
    Order updateOrderStatus(Long id, String status);
    void deleteOrder(Long id);
}