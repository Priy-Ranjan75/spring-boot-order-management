package com.example.order_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.order_management.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
