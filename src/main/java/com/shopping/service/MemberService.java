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
    private final RoleRepository roleRepository;

    @Transactional
    public void signup(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("기본 USER(ROLE)이 없습니다."));

        member.setRegDate(LocalDateTime.now());
        member.setUpdateDate(LocalDateTime.now());
        member.getRoles().add(userRole);
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
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
    }

    @Transactional
    public void updateProfile(Long memberId, String name, String phoneNumber, String address) {
        Member m = memberRepository.findById(memberId).get();
        if (name != null && !name.isBlank()) m.setName(name.trim());
        if (phoneNumber != null) m.setPhoneNumber(phoneNumber.trim());
        if (address != null) m.setAddress(address.trim());
        m.setUpdateDate(LocalDateTime.now());
    }
    @Transactional
    public void changePassword(Long memberId, String currentPassword, String newPassword) {
        Member m = memberRepository.findById(memberId).get();
        if (!m.getPassword().equals(currentPassword)) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }
        m.setPassword(newPassword);
        m.setUpdateDate(LocalDateTime.now());
    }
}