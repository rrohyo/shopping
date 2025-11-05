package com.shopping.controller;

import com.shopping.entity.Product;
import com.shopping.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    @GetMapping("/list")
    public String list(@RequestParam(required = false) String name, Model model) {
        List<Product> products;
        if (name != null && !name.isEmpty()) {
            products = productService.findByName(name);
            if (products.isEmpty()) {
                model.addAttribute("message", "검색 결과가 없습니다.");
            }
        } else {
            products = productService.findAll();
        }

        model.addAttribute("products", products);
        model.addAttribute("name", name);
        return "product/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/create";
    }


    @PostMapping("/create")
    public String create(HttpSession session, @ModelAttribute Product product) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        productService.create(
                memberId,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
        return "redirect:/home";
    }
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Product p = productService.findById(id);
        model.addAttribute("product", p);
        model.addAttribute("sessionMemberId", session.getAttribute("LOGIN_MEMBER_ID"));
        return "product/detail";
    }
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Product updatedProduct) {
        productService.update(id,
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getPrice(),
                updatedProduct.getStock());
        return "redirect:/product/detail/" + id;
    }
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/product/list";
    }
    /*
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam int price,
                         @RequestParam int stock) {
        productService.update(id, name, price, stock);
        return "redirect:/product/" + id;
    }*/
}
