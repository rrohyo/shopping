package com.shopping.repository;

import com.shopping.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderMemberId(Long memberId);
    List<OrderItem> findBySellerId(Long sellerId);
}