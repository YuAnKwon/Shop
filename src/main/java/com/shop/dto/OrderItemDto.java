package com.shop.dto;

import com.shop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 구매 이력 조회
public class OrderItemDto {
    private String itemNm;
    private Integer count;
    private Integer orderPrice;
    private String imgUrl;

    // orderItem 객체와 이미지 경로를 받아 세팅한다.
    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }

}
