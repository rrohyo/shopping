package com.shopping.service;

import com.shopping.entity.Product;
import com.shopping.entity.Qna;
import com.shopping.entity.QnaAnswer;
import com.shopping.repository.ProductRepository;
import com.shopping.repository.QnaAnswerRepository;
import com.shopping.repository.QnaRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final QnaAnswerRepository qnaAnswerRepository;

    @Transactional(readOnly = true)
    public List<Qna> getQnaList(Long productId) {
        return qnaRepository.findByProductId(productId);
    }
    @Transactional
    public Qna createQna(Long memberId, Long productId, String content) {
        Qna q = Qna.builder()
                .productId(productId)
                .memberId(memberId)
                .content(content)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        return qnaRepository.save(q);
    }

    @Transactional
    public void updateQna(Long qnaId, Long memberId, String content) {
        Qna q = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("질문이 없습니다."));
        if (!q.getMemberId().equals(memberId)) {
            throw new IllegalStateException("질문 작성자만 수정할 수 있습니다.");
        }
        q.setContent(content);
        q.setUpdateDate(LocalDateTime.now());
    }

    @Transactional
    public void deleteQna(Long qnaId, Long memberId) {
        Qna q = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("질문이 없습니다."));
        if (!q.getMemberId().equals(memberId)) {
            throw new IllegalStateException("질문 작성자만 삭제할 수 있습니다.");
        }
        qnaRepository.delete(q);
    }

    @Transactional
    public QnaAnswer createAnswer(Long qnaId, Long sellerId, Long productOwnerId, String content) {
        if (!sellerId.equals(productOwnerId)) {
            throw new IllegalStateException("판매자만 답변을 작성할 수 있습니다.");
        }
        Qna q = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("질문이 없습니다."));
        if (q.getAnswer() != null || qnaAnswerRepository.findByQnaId(qnaId).isPresent()) {
            throw new IllegalStateException("이미 답변이 존재합니다. 수정 기능을 사용하세요.");
        }
        QnaAnswer a = QnaAnswer.builder()
                .qna(q)
                .sellerId(sellerId)
                .content(content)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        return qnaAnswerRepository.save(a);
    }

    @Transactional
    public void updateAnswer(Long qnaId, Long sellerId, Long productOwnerId, String content) {
        if (!sellerId.equals(productOwnerId)) {
            throw new IllegalStateException("판매자만 수정할 수 있습니다.");
        }
        QnaAnswer a = qnaAnswerRepository.findByQnaId(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("답변이 없습니다."));
        if (!a.getSellerId().equals(sellerId)) {
            throw new IllegalStateException("해당 답변의 판매자만 수정할 수 있습니다.");
        }
        a.setContent(content);
        a.setUpdateDate(LocalDateTime.now());
    }

    @Transactional
    public void deleteAnswer(Long qnaId, Long sellerId, Long productOwnerId) {
        if (!sellerId.equals(productOwnerId)) {
            throw new IllegalStateException("판매자만 삭제할 수 있습니다.");
        }
        QnaAnswer a = qnaAnswerRepository.findByQnaId(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("답변이 없습니다."));
        if (!a.getSellerId().equals(sellerId)) {
            throw new IllegalStateException("해당 답변의 판매자만 삭제할 수 있습니다.");
        }
        qnaAnswerRepository.delete(a);
    }
}