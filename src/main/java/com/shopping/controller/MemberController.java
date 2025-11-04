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
            System.out.println("post login!!!!!");
            Member m = memberService.login(username, password);
            System.out.println("controller m === " + m);
            model.addAttribute("successMessage", "로그인 성공!!!!!");
            session.setAttribute("LOGIN_MEMBER_NAME", m.getName());
            session.setAttribute("LOGIN_MEMBER_ROLE", m.getRoles());
            session.setAttribute("LOGIN_MEMBER_ID", m.getId());
            return "redirect:/home";
        } catch (Exception e) {
            System.out.println("exception!!!!!!!!!");
            model.addAttribute("errorMessage", e.getMessage());// 화면에 에러 출력
            return "login";
        }
    }


    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/member/login";
    }
}