package com.shop.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

/* 인증 (Authentication)	-> 로그인 실패 (401 Unauthorized)
**Authentication Entry Point 란?**
- 인증되지 않은 유저가 인증이 필요한 자원을 요청했을 때 처리할 응답을 커스텀 --> 필터 체인에 등록
- 로그인 안 한 사용자가 인증이 필요한 자원(URL)에 접근했을 때 스프링 시큐리티의 **기본 동작(302 리다이렉트)**을 401 응답으로 바꾸기
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 인증x 이 인증필요한 자원 요청 시
        // as-is(default) : 302 반환 후 Location 헤더에 로그인 페이지 응답 -> 브라우저가 자동으로 로그인 페이지로 이동.
        // to-be : 401(UnAuthorized) 상태 코드에 적당한 메시지 반환.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 응답 객체에 상태코드 401 설정
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<body>");
        writer.println("<h1> 로그인이 필요한 페이지입니다.</h1>");
        writer.println("</body>");
        writer.println("</html>");
    }
}
