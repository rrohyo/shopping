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
    @JoinColumn(name = "qna_id")
    private Qna qna;

    private Long sellerId;
    @Lob
    private String content;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}