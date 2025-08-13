package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// 자신의 로컬 컴퓨터에 업로드한 파일을 찾을 위치 설정
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}") //application.properties에 설정한 uploadPath 값을 읽어온다.
    String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/images/**")
                // http 요청 "/images/**"이 들어오면
                // uploadPath(file:///C:/shop/)에서 동일한 리소스를 찾아서 반환한다.
                //  /images/photo.jpg 를 요청하면, C:/shop/photo.jpg가 반환된다.
                .addResourceLocations(uploadPath);
    }
}
