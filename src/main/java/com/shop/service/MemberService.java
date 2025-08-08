package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional //클래스 레벨에 붙이는 것은, 에러발생시 롤백시켜준다.
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member foundMember = memberRepository.findByEmail(member.getEmail());
        if(foundMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    // Spring Security가 비밀번호 비교 → 일치하면 로그인 성공, 아니면 실패
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // springsecurity에서 username은 흔히 ID라고 하는 정보를 의미. password는 비밀번호.

        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword()) //암호화된 비밀번호
                .roles(member.getRole().toString()) // 권한
                .build();
    }
}
