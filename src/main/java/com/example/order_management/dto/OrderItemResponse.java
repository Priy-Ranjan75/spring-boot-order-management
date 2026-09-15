package com.example.order_management.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

	private Long id;
	private Long productId;
	private String productName;
	private Integer quantity;
	private BigDecimal price;
	private BigDecimal itemTotal;

	public OrderItemResponse() {
	}

	public OrderItemResponse(Long id, Long productId, String productName, Integer quantity, BigDecimal price,
			BigDecimal itemTotal) {

		this.id = id;
		this.productId = productId;
		this.productName = productName;
		this.quantity = quantity;
		this.price = price;
		this.itemTotal = itemTotal;
	}

	public Long getId() {
		return id;
	}

	public Long getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getItemTotal() {
		return itemTotal;
	}
}