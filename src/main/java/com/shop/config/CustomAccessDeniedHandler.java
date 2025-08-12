package com.shop.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

/* 인가 (Authorization) -> 권한 부족 (403 Forbidden) (로그인했더라도 admin은 못들어가지~)
AccessDeniedHandler란?
- 인증된(Authenticated) 유저가 권한이 없는(Forbidden) 자원을 요청했을 때 동작
- 인증은 되었지만 권한이 부족해서 접근 거부(Access Denied) 당했을 때 처리하는 클래스.
*/

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 응답 객체에 상태코드 403 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<body>");
        writer.println("<h1>페이지 접근 권한이 없습니다.</h1>");
        writer.println("</body>");
        writer.println("</html>");
    }
}
