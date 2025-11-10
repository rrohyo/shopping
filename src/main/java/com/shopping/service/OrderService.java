package com.shopping.service;

import com.shopping.entity.Order;
import com.shopping.entity.OrderItem;
import com.shopping.entity.OrderStatus;
import com.shopping.entity.Product;
import com.shopping.repository.OrderItemRepository;
import com.shopping.repository.OrderRepository;
import com.shopping.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;


    @Transactional
    public void createOrder(Long memberId, Long productId, int quantity,
                            String deliveryAddress, String deliveryPhone) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (quantity <= 0) {
            throw new IllegalArgumentException("수량이 올바르지 않습니다.");
        }
        if (product.getStock() < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        Long sellerId = product.getMemberId();
        if (sellerId == null) {
            throw new IllegalStateException("판매자 정보가 없는 상품입니다.");
        }

        Order order = Order.builder()
                .memberId(memberId)
                .totalAmount((long) product.getPrice() * quantity)
                .status(OrderStatus.PAID)
                .deliveryAddress(deliveryAddress)
                .deliveryPhone(deliveryPhone)
                .orderDate(LocalDateTime.now())
                .build();
        orderRepository.save(order);

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .productId(productId)
                .sellerId(product.getMemberId())
                .productName(product.getName())
                .price((long) product.getPrice())
                .quantity(quantity)
                .subtotal((long) product.getPrice() * quantity)
                .status(OrderStatus.PAID)
                .build();
        orderItemRepository.save(orderItem);
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getMyOrders(Long memberId) {
        return orderItemRepository.findByOrderMemberId(memberId)
                .stream()
                .sorted(Comparator.comparing((OrderItem oi) -> oi.getOrder().getOrderDate()).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getMySales(Long sellerId) {
        return orderItemRepository.findBySellerId(sellerId)
                .stream()
                .sorted(Comparator.comparing(
                        (OrderItem oi) -> oi.getOrder().getOrderDate()
                ).reversed())
                .toList();
    }
    @Transactional
    public void updateOrderItemStatus(Long orderItemId, Long sellerId, OrderStatus status) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("주문 항목이 존재하지 않습니다."));

        if (!item.getSellerId().equals(sellerId)) {
            throw new IllegalStateException("본인이 판매한 상품만 상태를 변경할 수 있습니다.");
        }
        item.setStatus(status);
    }
}
