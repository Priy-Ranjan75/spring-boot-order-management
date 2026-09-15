package com.example.order_management.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.order_management.dto.OrderItemRequest;
import com.example.order_management.dto.OrderItemResponse;
import com.example.order_management.dto.OrderRequest;
import com.example.order_management.dto.OrderResponse;
import com.example.order_management.entity.AppUser;
import com.example.order_management.entity.Order;
import com.example.order_management.entity.OrderItem;
import com.example.order_management.entity.OrderStatus;
import com.example.order_management.entity.Product;
import com.example.order_management.exception.ProductNotFoundException;
import com.example.order_management.repository.AppUserRepository;
import com.example.order_management.repository.OrderRepository;
import com.example.order_management.repository.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            AppUserRepository appUserRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.appUserRepository = appUserRepository;
    }

    // =========================================================
    // CREATE ORDER
    // =========================================================

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        AppUser user = appUserRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username));

        if (request == null || request.getItems() == null
                || request.getItems().isEmpty()) {

            throw new RuntimeException("Order must contain at least one item");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {

            if (itemRequest.getQuantity() <= 0) {
                throw new RuntimeException(
                        "Quantity must be greater than zero");
            }

            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() ->
                            new ProductNotFoundException(
                                    "Product not found with id: "
                                            + itemRequest.getProductId()));

            // Check stock
            if (product.getStock() < itemRequest.getQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName()
                                + ". Available: "
                                + product.getStock()
                                + ", requested: "
                                + itemRequest.getQuantity());
            }

            // Product price at the time of order
            BigDecimal itemPrice = product.getPrice();

            // Calculate item total
            BigDecimal itemTotal = itemPrice.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);

            // Reduce product stock
            product.setStock(
                    product.getStock()
                            - itemRequest.getQuantity());

            productRepository.save(product);

            // Create OrderItem
            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(itemPrice);

            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return convertToOrderResponse(savedOrder);
    }

    // =========================================================
    // GET MY ORDERS
    // =========================================================

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        AppUser user = appUserRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username));

        List<Order> orders = orderRepository.findByUser(user);

        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders) {
            responses.add(convertToOrderResponse(order));
        }

        return responses;
    }

    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        AppUser user = appUserRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username));

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + orderId));

        // Make sure user can only see their own order
        if (!order.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not authorized to access this order");
        }

        return convertToOrderResponse(order);
    }

    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus newStatus) {

        if (newStatus == null) {
            throw new RuntimeException("Order status is required");
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        AppUser user = appUserRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username));

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + orderId));

        // Make sure user can only update their own order
        if (!order.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not authorized to update this order");
        }

        OrderStatus currentStatus = order.getStatus();

        // Do not allow same status
        if (currentStatus == newStatus) {

            throw new RuntimeException(
                    "Order is already in status: " + currentStatus);
        }

        // =====================================================
        // VALID STATUS TRANSITIONS
        //
        // PENDING -> CONFIRMED
        // CONFIRMED -> SHIPPED
        // SHIPPED -> DELIVERED
        //
        // CANCELLED is handled separately through DELETE
        // =====================================================

        boolean validTransition = switch (currentStatus) {

            case PENDING ->
                    newStatus == OrderStatus.CONFIRMED;

            case CONFIRMED ->
                    newStatus == OrderStatus.SHIPPED;

            case SHIPPED ->
                    newStatus == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED ->
                    false;
        };

        if (!validTransition) {

            throw new RuntimeException(
                    "Invalid order status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus);
        }

        order.setStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);

        return convertToOrderResponse(updatedOrder);
    }

    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        AppUser user = appUserRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username));

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + orderId));

        // Make sure user can only cancel their own order
        if (!order.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not authorized to cancel this order");
        }

        // Already cancelled
        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Order is already cancelled");
        }

        // Delivered orders cannot be cancelled
        if (order.getStatus() == OrderStatus.DELIVERED) {

            throw new RuntimeException(
                    "Delivered order cannot be cancelled");
        }

        // =====================================================
        // RESTORE PRODUCT STOCK
        // =====================================================

        for (OrderItem orderItem : order.getOrderItems()) {

            Product product = orderItem.getProduct();

            if (product != null) {

                int restoredStock =
                        product.getStock()
                                + orderItem.getQuantity();

                product.setStock(restoredStock);

                productRepository.save(product);
            }
        }

        // Change order status
        order.setStatus(OrderStatus.CANCELLED);

        Order cancelledOrder = orderRepository.save(order);

        return convertToOrderResponse(cancelledOrder);
    }

    // =========================================================
    // CONVERT ORDER ENTITY -> ORDER RESPONSE DTO
    // =========================================================

    private OrderResponse convertToOrderResponse(Order order) {

        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {

            Product product = item.getProduct();

            BigDecimal itemTotal =
                    item.getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity()));

            OrderItemResponse itemResponse =
                    new OrderItemResponse(
                            item.getId(),
                            product.getId(),
                            product.getName(),
                            item.getQuantity(),
                            item.getPrice(),
                            itemTotal
                    );

            itemResponses.add(itemResponse);
        }

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getUsername(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemResponses
        );
    }
}