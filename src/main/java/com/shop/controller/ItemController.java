package com.shop.controller;

import com.shop.dto.ItemDto;
import com.shop.dto.ItemFormDto;
import com.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    // 즉, 상품 등록 폼 페이지를 보여주기 위한 요청 핸들러
    @GetMapping("/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping("/admin/item/new")
    public String itemNew(@Valid @ModelAttribute ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){
        // 필수 입력 값이 없다면 다시 상품등록페이지로 반환.
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/"; //메인 페이지(/)로 이동 & 중복 요청 방지
    }

    // itemId의 상세페이지 화면
    @GetMapping("/admin/item/{itemId}")
    public String itemDtl(@PathVariable Long itemId, Model model){
        try {
            // 1. itemId로 DB 조회
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId); //Entity를 그대로 뷰에 보내지 않기 위해 DTO로 반환해야죠
            // 2. 조회 결과를 model에 담음
            model.addAttribute("itemFormDto", itemFormDto);
            // 3. 뷰 이름 반환
        }catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    @PostMapping("/admin/item/{itemId}")
    public String itemUpdate(@Valid @ModelAttribute ItemFormDto itemFormDto, BindingResult bindingResult,
                             Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/"; //메인 페이지(/)로 이동 & 중복 요청 방지
    }

}
