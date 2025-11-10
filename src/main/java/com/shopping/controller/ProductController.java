package com.shopping.controller;

import com.shopping.entity.Product;
import com.shopping.entity.Qna;
import com.shopping.entity.QnaAnswer;
import com.shopping.entity.Review;
import com.shopping.repository.ReviewRepository;
import com.shopping.service.ProductImageService;
import com.shopping.service.ProductService;
import com.shopping.service.QnaService;
import com.shopping.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;
    private final QnaService qnaService;
    private final ProductImageService productImageService;
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
    public String create(HttpSession session,
                         @ModelAttribute Product product) throws IOException {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) throw new IllegalStateException("로그인이 필요합니다.");

        Product saved = productService.create(
                memberId, product.getName(), product.getDescription(),
                product.getPrice(), product.getStock()
        );

        if (product.getImageFiles() != null && !product.getImageFiles().isEmpty()) {
            productImageService.saveImages(saved, product.getImageFiles());
        }

        return "redirect:/product/detail/" + saved.getId();
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {

        Product p = productService.findById(id);
        model.addAttribute("product", p);


        model.addAttribute("sessionMemberId", session.getAttribute("LOGIN_MEMBER_ID"));


        model.addAttribute("reviews", reviewService.getReviewsByProduct(id));
        model.addAttribute("reviewCount", reviewService.getReviewCount(id));
        double avg = reviewService.getAverageRating(id); // 0.0 ~ 5.0
        int fullStars = (int) Math.round(avg);
        fullStars = Math.max(0, Math.min(5, fullStars));
        int emptyStars = 5 - fullStars;

        model.addAttribute("avgRating", avg);
        model.addAttribute("fullStars", fullStars);
        model.addAttribute("emptyStars", emptyStars);

        model.addAttribute("qnaList", qnaService.getQnaList(id));

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
                         @ModelAttribute Product updatedProduct,
                         @RequestParam(value = "newImages", required = false) List<MultipartFile> images,
                         @RequestParam(value = "deleteImageIds", required = false) List<Long> deleteImageIds) {

        productService.update(
                id,
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getPrice(),
                updatedProduct.getStock()
        );

        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            for (Long imageId : deleteImageIds) {
                productImageService.deleteImage(imageId);
            }
        }
        if (images != null && !images.isEmpty() && !images.get(0).isEmpty()) {
            Product product = productService.findById(id);
            try {
                productImageService.saveImages(product, images);
            } catch (IOException e) {
                System.err.println("이미지 업로드 실패: " + e.getMessage());
            }
        }

        return "redirect:/product/detail/" + id;
    }
    @PostMapping("/delete/{id}")
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
