package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;

public class OrderServiceTest {
	
	//リポジトリクラスをモック化する
	@Mock
	private OrderRepository orderRepository;
	
	//テスト対象クラスの依存関係を注入
	@InjectMocks
	private OrderServiceImpl orderService;
	
	@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito の初期化
    }
	
	@Test
	void getAllOrders() throws Exception{
        when(orderRepository.findAll()).thenReturn(List.of(new Order(1L, "Tanaka", "Laptop", 1, "PENDING")));

        List<Order> orders = orderService.getAllOrders();

        assertEquals(1, orders.size());
        assertEquals("Tanaka", orders.get(0).getCustomerName());
		
	}
}
