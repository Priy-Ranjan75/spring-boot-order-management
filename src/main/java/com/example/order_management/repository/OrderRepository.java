package com.example.order_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.order_management.entity.AppUser;
import com.example.order_management.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(AppUser user);
}