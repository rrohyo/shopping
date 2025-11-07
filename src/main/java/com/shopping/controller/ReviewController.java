package com.shopping.controller;

import com.shopping.entity.Member;
import com.shopping.entity.OrderItem;
import com.shopping.entity.Product;
import com.shopping.repository.MemberRepository;
import com.shopping.service.ProductService;
import com.shopping.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final MemberRepository memberRepository;

    @GetMapping("/create")
    public String writeForm(@RequestParam Long productId,
                            HttpSession session,
                            Model model) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        model.addAttribute("product", productService.findById(productId));
        model.addAttribute("member", memberRepository.findById(memberId).orElse(null));
        return "review";
    }

    @PostMapping("/create")
    public String create(@RequestParam Long productId,
                         @RequestParam int rating,
                         @RequestParam String content,
                         HttpSession session,
                         Model model) {

        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        try {
            reviewService.createReview(memberId, productId, rating, content);
            return "redirect:/product/detail/" + productId + "?tab=reviews";
        } catch (RuntimeException e) {
            model.addAttribute("product", productService.findById(productId));
            model.addAttribute("member", memberRepository.findById(memberId).orElse(null));
            model.addAttribute("errorMessage", e.getMessage());
            return "review";
        }
    }

    @PostMapping("/update")
    public String update(@RequestParam Long reviewId,
                         @RequestParam Long productId,
                         @RequestParam int rating,
                         @RequestParam String content,
                         HttpSession session) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        reviewService.updateReview(reviewId, memberId, rating, content);
        return "redirect:/product/detail/" + productId + "?tab=reviews";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long reviewId,
                         @RequestParam Long productId,
                         HttpSession session) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        reviewService.deleteReview(reviewId, memberId);
        return "redirect:/product/detail/" + productId + "?tab=reviews";
    }
}