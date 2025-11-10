package com.shopping.controller;

import com.shopping.entity.Member;
import com.shopping.entity.OrderStatus;
import com.shopping.entity.Product;
import com.shopping.repository.MemberRepository;
import com.shopping.service.OrderService;
import com.shopping.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final MemberRepository memberRepository;

    @GetMapping("/order/{productId}")
    public String orderForm(@PathVariable Long productId, HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        Product product = productService.findById(productId);
        Member member = memberRepository.findById(memberId).orElse(null);

        model.addAttribute("product", product);
        model.addAttribute("member", member);
        return "order";
    }

    @PostMapping("/order/create")
    public String createOrder(@RequestParam Long productId,
                              @RequestParam int quantity,
                              @RequestParam String deliveryAddress,
                              @RequestParam String deliveryPhone,
                              HttpSession session,
                              Model model) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        try {
            orderService.createOrder(memberId, productId, quantity, deliveryAddress, deliveryPhone);
            return "redirect:/member/mypage";
        } catch (IllegalStateException e) {
            Product product = productService.findById(productId);
            Member member = memberRepository.findById(memberId).orElse(null);
            model.addAttribute("product", product);
            model.addAttribute("member", member);
            model.addAttribute("errorMessage", e.getMessage());
            return "order";
        }
    }
    @PostMapping("/order/status")
    public String updateOrderStatus(@RequestParam Long orderItemId,
                                    @RequestParam OrderStatus status,
                                    HttpSession session) {
        Long sellerId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (sellerId == null) return "redirect:/member/login";

        orderService.updateOrderItemStatus(orderItemId, sellerId, status);
        return "redirect:/member/mypage?tab=sales";
    }
}