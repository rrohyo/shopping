package com.shopping.service;

import com.shopping.entity.Member;
import com.shopping.entity.Role;
import com.shopping.repository.MemberRepository;
import com.shopping.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void join(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        if (memberRepository.existsByLoginId(member.getLoginId())) {
            throw new RuntimeException("이미 사용 중인 로그인ID입니다.");
        }
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            throw new RuntimeException("USER 권한이 존재하지 않습니다.");
        }
        member.setRole(userRole);
        member.setRegDate(LocalDateTime.now());
        member.setUpdateDate(LocalDateTime.now());
        memberRepository.save(member);
        memberRepository.save(member);
    }
    @Transactional(readOnly = true)
    public Member login(String loginId, String password) {
        Member m = memberRepository.findByLoginId(loginId).orElse(null);
        if (m == null) {
            throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!m.getLoginPw().equals(password)) {
            throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return m;
    }
}