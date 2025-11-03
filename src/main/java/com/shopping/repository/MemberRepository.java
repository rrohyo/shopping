// src/main/java/com/shopping/repository/MemberRepository.java
package com.shopping.repository;

import com.shopping.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndPassword(String email, String Password);

    boolean existsByEmail(String email);
}