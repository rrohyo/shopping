package com.shopping.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/login")
    public String showMain(HttpSession session, Model model) {
        return "login";
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login";
    }
}