package com.shopping.service;

import com.shopping.entity.Product;
import com.shopping.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    @Transactional
    public Product create(Long memberId, String name, int price, int stock) {
        Product p = Product.builder()
                .memberId(memberId)
                .name(name)
                .price(price)
                .stock(stock)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        return productRepository.save(p);
    }
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    @Transactional(readOnly = true)
    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }
    @Transactional
    public Product update(Long id, String name, int price, int stock) {
        Product p = findById(id);
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);
        p.setUpdateDate(LocalDateTime.now());
        return productRepository.save(p);
    }
    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}