package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
     // 회원가입시 중복이메일 불가
     Member findByEmail(String email);

    String email(String email);
}
