package com.shopping.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/login")
    public String showMain(HttpSession session, Model model) {
        model.addAttribute("memberId",   session.getAttribute("LOGIN_MEMBER_ID"));
        model.addAttribute("memberName", session.getAttribute("LOGIN_MEMBER_NAME"));
        model.addAttribute("memberRole", session.getAttribute("LOGIN_MEMBER_ROLE"));
        return "login";
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login";
    }
}