package com.shop.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// 현재 로그인한 사용자 정보 반환
// JPA가 @CreatedBy나 @LastModifiedBy 값을 넣어줄 때 "누가" 했는지를 여기서 가져감

public class AuditorAwareImpl implements AuditorAware<String> {

    /*
    Entity 생성 및 수정 시에 해당 행위의 주체(유저)의 정보를 알아내는 역할
    구현하려면 : sequrity context 에 Authentication을 꺼내면 user정보가 있음. => userid나 name등을 반환하면 됨.
    */
    @Override
    public Optional<String> getCurrentAuditor() {
        // SecurityContext에서 현재 인증 정보를 가져옴
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userId = "";

        // 인증 정보가 존재하면
        if(authentication!=null){
            userId = authentication.getName();
        }

        // 사용자 ID를 Optional로 감싸 반환
        return Optional.of(userId);
    }
}
