package com.shopping.service;

import com.shopping.entity.Member;
import com.shopping.entity.Role;
import com.shopping.repository.MemberRepository;
import com.shopping.repository.RoleRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void join(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        member.setRegDate(LocalDateTime.now());
        member.setUpdateDate(LocalDateTime.now());
        member = memberRepository.save(member);
        Set<Role> userRoles = Set.of(new Role(Math.toIntExact(member.getId()), "USER"));
        member.setRoles(userRoles);
        memberRepository.save(member);
    }
    @Transactional(readOnly = true)
    public Member login(String loginId, String password) {
        Member m = memberRepository.findByEmailAndPassword(loginId, password).orElse(null);
        if (m == null) {
            throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return m;
    }
}