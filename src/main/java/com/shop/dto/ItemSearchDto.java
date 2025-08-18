package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 상품관리
public class ItemSearchDto {

    private String searchDateType; // 현재시간과 상품 등록일을 비교해서 조회

    private ItemSellStatus searchSellStatus; // 상품의 판매상태

    private String searchBy; // 어떤 유형으로 조회할지

    private String searchQuery = ""; //검색키워드
    // = "" 한 이유 : 검색키워드가 없을때도 있을것.

}