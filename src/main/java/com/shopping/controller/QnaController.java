package com.shopping.controller;

import com.shopping.service.ProductService;
import com.shopping.service.QnaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnaController {

    private final QnaService qnaService;
    private final ProductService productService;   // 상품 소유자 확인용

    @PostMapping("/create")
    public String create(@RequestParam Long productId,
                         @RequestParam String content,
                         HttpSession session) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        qnaService.createQna(memberId, productId, content);
        return "redirect:/product/detail/" + productId + "?tab=qna";
    }

    @PostMapping("/update")
    public String update(@RequestParam Long productId,
                         @RequestParam Long qnaId,
                         @RequestParam String content,
                         HttpSession session) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        qnaService.updateQna(qnaId, memberId, content);
        return "redirect:/product/detail/" + productId + "?tab=qna";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long productId,
                         @RequestParam Long qnaId,
                         HttpSession session) {
        Long memberId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (memberId == null) return "redirect:/login";

        qnaService.deleteQna(qnaId, memberId);
        return "redirect:/product/detail/" + productId + "?tab=qna";
    }

    @PostMapping("/answer/create")
    public String answerCreate(@RequestParam Long productId,
                               @RequestParam Long qnaId,
                               @RequestParam String content,
                               HttpSession session) {
        Long sellerId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (sellerId == null) return "redirect:/login";

        Long productOwnerId = productService.findById(productId).getMemberId();
        qnaService.createAnswer(qnaId, sellerId, productOwnerId, content);
        return "redirect:/product/detail/" + productId + "?tab=qna";
    }

    @PostMapping("/answer/update")
    public String answerUpdate(@RequestParam Long productId,
                               @RequestParam Long qnaId,
                               @RequestParam String content,
                               HttpSession session) {
        Long sellerId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (sellerId == null) return "redirect:/login";

        Long productOwnerId = productService.findById(productId).getMemberId();
        qnaService.updateAnswer(qnaId, sellerId, productOwnerId, content);
        return "redirect:/product/detail/" + productId + "?tab=qna";
    }
    @PostMapping("/answer/delete")
    public String answerDelete(@RequestParam Long productId,
                               @RequestParam Long qnaId,
                               HttpSession session) {
        Long sellerId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (sellerId == null) return "redirect:/login";

        Long productOwnerId = productService.findById(productId).getMemberId();
        qnaService.deleteAnswer(qnaId, sellerId, productOwnerId);
        return "redirect:/product/detail/" + productId + "?tab=qna";
    }
}