package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

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
        
        // OrderRepositoryモックの振る舞いを設定
        when(orderRepository.existsById(1L))
            .thenReturn(true);

        // deleteById(1L) が呼ばれても何もしないモック設定
        doNothing().when(orderRepository).deleteById(1L);
    }
	
	@Test
	void getAllOrders() throws Exception{
        when(orderRepository.findAll()).thenReturn(List.of(new Order(1L, "Tanaka", "Laptop", 1, "PENDING")));

        List<Order> orders = orderService.getAllOrders();

        assertEquals(1, orders.size());
        assertEquals("Tanaka", orders.get(0).getCustomerName());
		
	}
	
	@Test
	void createOrder() throws Exception{
		Order order = new Order(null, "Suzuki", "Phone", 2, "PENDING") ;
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder("Suzuki", "Phone", 2);

        assertNotNull(result);
        assertEquals("Suzuki", result.getCustomerName());
        assertEquals("Phone", result.getProductName());
		
	}
	
	@Test
	void updateOrder() throws Exception{
		Order order = new Order(null, "Suzuki", "Phone", 2, "PENDING") ;
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus(1L,"COMPLETED");

        assertNotNull(result);
        assertEquals("Suzuki", result.getCustomerName());
        assertEquals("Phone", result.getProductName());
		
	}
	//削除メソッドのテストコード作成
	@Test
	void deleteOrder() throws Exception{
		orderService.deleteOrder(1L);
		verify(orderRepository, times(1)).deleteById(1L);
	}
	//存在しないIDを指定した場合のテストコード作成
	@Test
	void deleteOrderWithInvalidId() throws Exception{
		assertThrows(Exception.class, () -> orderService.deleteOrder(2L));
	}
	
}
