package com.shopping.controller;

import com.shopping.entity.Product;
import com.shopping.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public String list(@RequestParam(required = false) String name,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "latest") String sort,
                       Model model) {

        page = Math.max(page, 0);

        Page<Product> pageResult = productService.getList(name, page, size, sort);

        if (pageResult.getTotalPages() > 0 && page >= pageResult.getTotalPages()) {
            int last = pageResult.getTotalPages() - 1;
            pageResult = productService.getList(name, last, size, sort);
        }

        model.addAttribute("product", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("name", name);
        model.addAttribute("sort", sort);
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
