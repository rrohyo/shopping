package com.shopping.controller;

import com.shopping.entity.Member;
import com.shopping.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    @GetMapping("/join")
    public String joinForm() {
        return "member/join";
    }
    @PostMapping("/join")
    public String join(@ModelAttribute Member member, Model model) {
        try {
            memberService.join(member);
            return "redirect:/member/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage()); // 화면에 에러 출력
            return "member/join";
        }
    }
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            Member m = memberService.login(loginId, password);
            session.setAttribute("LOGIN_MEMBER_ID", m.getId());
            session.setAttribute("LOGIN_MEMBER_NAME", m.getName());
            session.setAttribute("LOGIN_MEMBER_ROLE", m.getRole().getName());
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage()); // 화면에 에러 출력
            return "member/login";
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/member/login";
    }
}