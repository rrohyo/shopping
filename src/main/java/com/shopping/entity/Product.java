package com.shopping.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private int price;

    private int stock;

    private LocalDateTime regDate;

    private LocalDateTime updateDate;
}