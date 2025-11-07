package com.shopping.repository;

import com.shopping.entity.QnaAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Long> {
    Optional<QnaAnswer> findByQnaId(Long qnaId);
    void deleteByQnaId(Long qnaId);
}