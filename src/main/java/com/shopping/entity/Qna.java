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
public class Qna {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;   // 어떤 상품의 QnA인지
    private Long memberId;    // 질문 작성자(회원 ID)
    private String content;   // 질문 내용
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    @OneToOne(mappedBy = "qna", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QnaAnswer answer; // 판매자 답변(없을 수 있음)
}