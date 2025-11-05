package com.shopping.controller;

import com.shopping.entity.Product;
import com.shopping.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping("/login")
    public String showMain(HttpSession session, Model model) {
        return "login";
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(@RequestParam(required = false) String name,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        Page<Product> result = productService.getList(name, page, 9, "latest");

        model.addAttribute("product", result.getContent());
        model.addAttribute("currentPage", result.getNumber());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("name", name);

        return "home";
    }
}