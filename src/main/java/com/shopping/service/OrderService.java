package com.shopping.service;

import com.shopping.entity.Order;
import com.shopping.entity.OrderItem;
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
                .status("PENDING")
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
                .status("PENDING")
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
}
