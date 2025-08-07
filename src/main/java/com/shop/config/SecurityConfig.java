package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 스프링 설정 클래스
// 이 클래스 안에 작성된 메서드들 중 @Bean이 붙은 메서드의 반환값을 Spring 컨테이너가 Bean으로 등록한다는 뜻

@EnableWebSecurity // Spring Security를 사용하겠다
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        // Spring Security에서 보안 필터 체인을 수동 설정. HttpSecurity를 이용해서 요청 URL에 대한 보안 정책을 설정
        // http.설정1
        // http.설정2
        // http.설정3
        return http.build(); // 위에서 설정한 내용을 바탕으로 SecurityFilterChain 객체를 생성하여 반환.
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); // 해시 함수를 이용하여 암호화하여 저장.
        // 단방향 암호화.(복호화 불가능). 로그인할때마다 hash함수로 유사성 비교.(동일x)
    }
}
