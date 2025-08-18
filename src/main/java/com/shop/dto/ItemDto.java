package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemDto {
    private Long id; //상품 코드

    private String itemNm;

    private Integer price;

    private String itemDetail;

    private String sellStated;

    private LocalDateTime regTime; // 상품 등록 시간

    private LocalDateTime updateTime;; // 상품 수정 시간
}
