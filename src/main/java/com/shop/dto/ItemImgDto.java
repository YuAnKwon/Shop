package com.shop.dto;

import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repimgYn;

    private static ModelMapper modelMapper = new ModelMapper(); //멤버 변수로 modelMapper 객체 추가.
    // ModelMapper 라이브러리를 멤버 변수로 static하게 하나만 만들어 재사용

    // Entity -> Dto
    public static ItemImgDto of(ItemImg itemImg){
        return modelMapper.map(itemImg, ItemImgDto.class);
    }
}
