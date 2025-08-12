package com.shop.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
                .loginPage("/members/login") // 인증 필요시 리다이렉트(302)할 페이지 //API에서는 302보다 401을 줘야 프론트에서 “아, 인증 만료네”라고 처리 가능.
                .defaultSuccessUrl("/") //로그인에 성공했을때, 메인페이지로 보낸다.
                .usernameParameter("email") //로그인 시 사용할 파라미터 이름으로 email을 지정. (username -> email)
                .failureUrl("/members/login/error") // 로그인 실패시 이동할 url
        );

        // 로그아웃 설정.
        http.logout((it)->it
                .logoutUrl("/members/logout") //로그아웃 url
                .logoutSuccessUrl("/") // 로그아웃 성공시 이동할 url
        );

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/members/**","/item/**","/images/**").permitAll() //permitAll을 통해 모든 사용자가 인증(로그인)없이 해당 경로에 접근 가능.
                .requestMatchers("/admin/**").hasRole("ADMIN") //admin으로 시작하는 경로는 admin role일 경우에만 접근 가능하도록.
                .anyRequest().authenticated() // 위에서 설정해준 경로를 제외한 나머지 경로는 모두 인증을 요구하도록.
        );

        // authenticationEntryPoint 등록하는 과정.
        http.exceptionHandling(e -> e
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );



        /* CSRF(Cross-Site Request Forgery) 보호 기능을 끄는 설정
         http.csrf(AbstractHttpConfigurer::disable); */
        return http.build(); // 위에서 설정한 내용을 바탕으로 SecurityFilterChain 객체를 생성하여 반환.
    }

    //화면을 정상적으로 그리는데 필요한 정적인 자원들을 허용. 특별히 보안적인 고려사항이 없는 웹 전용 자원들
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); //static 디렉터리의 하위 파일은 인증을 무시하도록.
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); // 해시 함수를 이용하여 암호화하여 저장.
        // 단방향 암호화.(복호화 불가능). 로그인할때마다 hash함수로 유사성 비교.(동일x)
    }
}
