package com.shopping.service;

import com.shopping.entity.Product;
import com.shopping.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    @Transactional
    public Product create(Long memberId, String name,String description, int price, int stock) {
        Product product = Product.builder()
                .memberId(memberId)
                .name(name)
                .price(price)
                .description(description)
                .stock(stock)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        return productRepository.save(product);
    }
    @Transactional(readOnly = true)
    public Page<Product> getList(String nameKeyword, int page, int size, String sortKey) {
        Pageable pageable = buildPageable(page, size, sortKey);

        if (nameKeyword != null && !nameKeyword.isBlank()) {
            Page<Product> result = productRepository
                    .findByNameContainingIgnoreCase(nameKeyword.trim(), pageable);
            return result.hasContent() ? result : productRepository.findAll(pageable);
        }
        return productRepository.findAll(pageable);
    }
    @Transactional(readOnly = true)
    public Page<Product> getProduct(Long memberId, int page, int size, String sortKey) {
        Pageable pageable = buildPageable(page, size, sortKey);
        return productRepository.findByMemberId(memberId, pageable);
    }

    private Pageable buildPageable(int page, int size, String sortKey) {
        Sort sort;
        if ("name".equalsIgnoreCase(sortKey)) sort = Sort.by(Sort.Direction.ASC, "name");
        else if ("price-low".equalsIgnoreCase(sortKey)) sort = Sort.by(Sort.Direction.ASC, "price");
        else if ("price-high".equalsIgnoreCase(sortKey)) sort = Sort.by(Sort.Direction.DESC, "price");
        else if ("stock".equalsIgnoreCase(sortKey)) sort = Sort.by(Sort.Direction.DESC, "stock");
        else sort = Sort.by(Sort.Direction.DESC, "regDate");
        return PageRequest.of(Math.max(page,0), Math.max(size,1), sort);
    }
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    @Transactional
    public Product update(Long id, String name,String description, int price, int stock) {
        Product p = findById(id);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setUpdateDate(LocalDateTime.now());
        return productRepository.save(p);
    }
    @Transactional
    public void delete(Long id) {

        productRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public List<Product> findByMemberId(Long memberId) {

        return productRepository.findByMemberId(memberId);
    }
}