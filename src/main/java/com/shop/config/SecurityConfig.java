package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 스프링 설정 클래스
// 이 클래스 안에 작성된 메서드들 중 @Bean이 붙은 메서드의 반환값을 Spring 컨테이너가 Bean으로 등록한다는 뜻

@EnableWebSecurity // Spring Security를 사용하겠다
public class SecurityConfig {
    @Bean
    // Spring Security에서 보안 필터 체인을 수동 설정. HttpSecurity를 이용해서 요청 URL에 대한 보안 정책을 설정
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        // 로그인 설정.
        http.formLogin((it)-> it
                .loginPage("/members/login") //로그인안한 유저가 로그인이 필요한 페이지를 볼때, 로그인하라고 안내할 페이지
                .defaultSuccessUrl("/") //로그인에 성공했을때, 메인페이지로 보낸다.
                .usernameParameter("email") //로그인 시 사용할 파라미터 이름으로 email을 지정. (username -> email)
                .failureUrl("/members/login/error") // 로그인 실패시 이동할 url
        );

        // 로그아웃 설정.
        http.logout((it)->it
                .logoutUrl("/members/logout") //로그아웃 url
                .logoutSuccessUrl("/") // 로그아웃 성공시 이동할 url
        );

        // CSRF(Cross-Site Request Forgery) 보호 기능을 끄는 설정
        // http.csrf(AbstractHttpConfigurer::disable);
        return http.build(); // 위에서 설정한 내용을 바탕으로 SecurityFilterChain 객체를 생성하여 반환.
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); // 해시 함수를 이용하여 암호화하여 저장.
        // 단방향 암호화.(복호화 불가능). 로그인할때마다 hash함수로 유사성 비교.(동일x)
    }
}
