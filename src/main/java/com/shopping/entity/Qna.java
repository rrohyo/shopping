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

    private Long productId;
    private Long memberId;
    private String content;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    @OneToOne(mappedBy = "qna", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QnaAnswer answer;
}