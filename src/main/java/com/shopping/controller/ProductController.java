package com.shopping.controller;

import com.shopping.entity.Product;
import com.shopping.service.ProductService;
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
        model.addAttribute("searchKeyword", name);
        return "product/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/create";
    }


    @PostMapping("/create")
    public String create(@ModelAttribute Product product) {
        productService.create(
                product.getMemberId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
        return "redirect:/product/list";
    }
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product p = productService.findById(id);
        model.addAttribute("product", p);
        return "product/detail";
    }
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Product updatedProduct) {
        productService.update(id,
                updatedProduct.getName(),
                updatedProduct.getPrice(),
                updatedProduct.getStock());
        return "redirect:/product/" + id;
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