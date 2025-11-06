package com.shopping.controller;

import com.shopping.entity.Member;
import com.shopping.entity.OrderItem;
import com.shopping.entity.Product;
import com.shopping.repository.MemberRepository;
import com.shopping.repository.OrderItemRepository;
import com.shopping.service.MemberService;
import com.shopping.service.OrderService;
import com.shopping.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final ProductService productService;
    private final MemberRepository memberRepository;
    private final OrderService orderService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("member", new Member());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute Member member, Model model) {
        try {
            memberService.signup(member);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            Member m = memberService.login(username, password);
            model.addAttribute("successMessage", "로그인 성공!");
            session.setAttribute("LOGIN_MEMBER_NAME", m.getName());
            session.setAttribute("LOGIN_MEMBER_ROLE", m.getRoles());
            session.setAttribute("LOGIN_MEMBER_ID", m.getId());
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/member/login";

        Member member = memberRepository.findById(memberId).orElseThrow();
        List<Product> product = productService.findByMemberId(memberId);
        List<OrderItem> orderItems = orderService.getMyOrders(memberId);
        List<OrderItem> salesItems = orderService.getMySales(memberId);

        model.addAttribute("member", member);
        model.addAttribute("product", product);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("salesItems", salesItems);

        return "mypage";
    }

    @PostMapping("/mypage/update")
    public String updateMyPage(HttpSession session,
                               @RequestParam String name,
                               @RequestParam String phoneNumber,
                               @RequestParam String address) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        memberService.updateProfile(memberId, name, phoneNumber, address);
        return "redirect:/member/mypage";
    }

    @PostMapping("/mypage/password")
    public String changePassword(HttpSession session,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "새 비밀번호가 일치하지 않습니다.");
            return "mypage";
        }
        try {
            memberService.changePassword(memberId, currentPassword, newPassword);
            model.addAttribute("successMessage", "비밀번호가 성공적으로 변경되었습니다.");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "비밀번호 설정 오류입니다.");
        }
        return "redirect:/login";
    }
}