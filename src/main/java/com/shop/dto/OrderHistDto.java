package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
// 주문 정보를 담기
public class OrderHistDto {

    // Order 엔티티를 받아서 DTO로 변환
    public OrderHistDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    // 주문 상품 DTO를 리스트에 추가하는 메서드
    public void addOrderItemDto(OrderItemDto orderItemDto){
        this.orderItemDtoList.add(orderItemDto);
    }
}
