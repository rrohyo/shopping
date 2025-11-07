package com.shopping.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QnaAnswer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "qna_id") // qna_answer.qna_id FK
    private Qna qna;

    private Long sellerId;        // 판매자(상품 등록자) ID
    @Lob
    private String content;       // 답변 내용
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}