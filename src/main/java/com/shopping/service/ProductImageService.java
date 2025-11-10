package com.shopping.service;

import com.shopping.entity.Product;
import com.shopping.entity.ProductImage;
import com.shopping.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final String uploadDir = "/Users/sanghyun/Downloads/files";

    public List<ProductImage> saveImages(Product product, List<MultipartFile> files) throws IOException {
        List<ProductImage> images = new ArrayList<>();
        if (files == null || files.isEmpty()) return images;

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String imageName = file.getOriginalFilename();
                File filePath = new File(uploadDir, imageName);
                file.transferTo(filePath);

                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageName(imageName)
                        .url("/uploads/" + imageName)
                        .build();

                productImageRepository.save(image);
                images.add(image);
            }
        }
        return images;
    }

    public void deleteImage(Long imageId) {
        productImageRepository.deleteById(imageId);
    }
    public void updateImages(Product product, List<MultipartFile> files) throws IOException {
        productImageRepository.deleteByProductId(product.getId());
        if (files != null && !files.isEmpty()) {
            saveImages(product, files);
        }
    }
    public String getImageUrl(Long productId) {   // ← 더 일반적이고 자연스러운 이름
        if (productId == null) return null;

        List<ProductImage> images = productImageRepository.findByProductId(productId);
        if (images == null || images.isEmpty()) return null;

        ProductImage first = images.get(0);
        if (first.getUrl() != null && !first.getUrl().isEmpty()) {
            return first.getUrl();
        }
        if (first.getImageName() != null && !first.getImageName().isEmpty()) {
            return "/uploads/" + first.getImageName();
        }
        return null;
    }
}