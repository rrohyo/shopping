package com.shopping.service;

import com.shopping.entity.OrderItem;
import com.shopping.entity.Review;
import com.shopping.repository.OrderItemRepository;
import com.shopping.repository.ReviewRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    public long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    @Transactional(readOnly = true)
    public double getAverageRating(Long productId) {
        List<Review> list = reviewRepository.findByProductId(productId);
        if (list.isEmpty()) return 0.0;
        return list.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    @Transactional
    public Review createReview(Long memberId, Long productId, int rating, String content) {
        OrderItem orderItem = orderItemRepository.findByOrderMemberId(memberId).stream()
                .filter(oi -> oi.getProductId().equals(productId))
                .filter(oi -> !reviewRepository.existsByOrderItemId(oi.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("구매 이력이 없거나 리뷰를 이미 작성했습니다"));

        int validRating = (rating >= 1 && rating <= 5) ? rating : 1;

        Review review = Review.builder()
                .productId(productId)
                .memberId(memberId)
                .orderItemId(orderItem.getId())
                .rating(validRating)
                .content(content)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        return reviewRepository.save(review);
    }

    /* 별점없이
    @Transactional
    public void updateReview(Long reviewId, Long memberId, String content) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        if (!review.getMemberId().equals(memberId)) {
            throw new IllegalStateException("본인 리뷰만 수정할 수 있습니다.");
        }
        review.setContent(content);
        review.setUpdateDate(LocalDateTime.now());
        reviewRepository.save(review);
    } */


    @Transactional
    public void updateReview(Long reviewId, Long memberId, int rating, String content) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        if (!review.getMemberId().equals(memberId))
            throw new IllegalStateException("본인 리뷰만 수정할 수 있습니다.");

        int validRating = (rating >= 1 && rating <= 5) ? rating : 1; // ★ 별점 유효범위 보정
        review.setRating(validRating);
        review.setContent(content);
        review.setUpdateDate(LocalDateTime.now());
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        if (!review.getMemberId().equals(memberId))
            throw new IllegalStateException("본인 리뷰만 삭제할 수 있습니다.");

        reviewRepository.delete(review);
    }
}