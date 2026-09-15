package com.example.order_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.order_management.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}