package com.example.order_management.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.order_management.dto.ProductRequest;
import com.example.order_management.dto.ProductResponse;
import com.example.order_management.entity.Product;
import com.example.order_management.exception.ProductNotFoundException;
import com.example.order_management.repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	private ProductResponse mapToResponse(Product product) {

		return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
				product.getStock(), product.getCreatedAt(), product.getUpdatedAt());
	}

	public ProductResponse createProduct(Product product) {
		Product savedProduct = productRepository.save(product);

		return mapToResponse(savedProduct);
	}

	public List<ProductResponse> getAllProducts() {

		return productRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	public ProductResponse getProductById(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

		return mapToResponse(product);
	}

	public ProductResponse updateProduct(Long id, ProductRequest request) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());

		Product updatedProduct = productRepository.save(product);

		return mapToResponse(updatedProduct);
	}

	public void deleteProduct(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

		productRepository.delete(product);
	}
}