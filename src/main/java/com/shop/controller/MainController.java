package com.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String main(Model model){
        //유저가 요청한 것에 따라 조회하려는 이미지의 파일명을 동적으로 전달.
        String imgSrc = "pikmin.png";
        model.addAttribute( "imgPath",imgSrc);
        return "main"; //main이라는 뷰 이름을 찾는다.
    }
}
