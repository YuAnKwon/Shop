package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {
    private Long id;

    private String itemNm;

    private String itemDetail;

    private String imgUrl;

    private Integer price;

}
