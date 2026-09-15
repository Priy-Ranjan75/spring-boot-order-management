package com.example.order_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.order_management.entity.OrderStatus;

public class OrderResponse {

    private Long id;
    private Long userId;
    private String username;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;

    public OrderResponse() {
    }

    public OrderResponse(
            Long id,
            Long userId,
            String username,
            OrderStatus status,
            BigDecimal totalAmount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<OrderItemResponse> items) {

        this.id = id;
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}