package com.shopping.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long memberId;
    private Long orderItemId;
    private int rating;
    private String content;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}