package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginMember(){
        return "member/memberLoginForm";
    }

    @GetMapping("/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를확인해주세요.");
        return "member/memberLoginForm";
    }

    @GetMapping("/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping("/new")
    public String memberForm(@ModelAttribute @Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){  // memberFormDto 객체에 폼 데이터가 자동으로 채워줌.
        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }

        try{
            Member member = Member.createMember(memberFormDto, passwordEncoder); //dto -> entity
            memberService.saveMember(member); //entity 전달.
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/"; // 컨트롤러 메서드가 처리 후 user를 다른 URL로 이동시키는 방법
    }

}
