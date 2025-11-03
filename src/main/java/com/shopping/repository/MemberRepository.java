// src/main/java/com/shopping/repository/MemberRepository.java
package com.shopping.repository;

import com.shopping.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);

    boolean existsByEmail(String email);
    boolean existsByLoginId(String loginId);
}