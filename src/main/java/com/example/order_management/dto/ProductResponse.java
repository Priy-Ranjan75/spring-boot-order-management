package com.example.order_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponse {

	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private Integer stock;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public ProductResponse() {
	}

	public ProductResponse(Long id, String name, String description, BigDecimal price, Integer stock,
			LocalDateTime createdAt, LocalDateTime updatedAt) {

		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Integer getStock() {
		return stock;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}