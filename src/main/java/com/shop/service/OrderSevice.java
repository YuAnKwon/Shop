package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.order.OrderRepository;
import com.shop.repository.item.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderSevice {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String email){
        //1. orderDto.itemID ==> item 엔티티 조회
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        //2. email ==> member 엔티티 조회
        Member member = memberRepository.findByEmail(email);

        //3. orderitem 엔티티 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        //4. order 엔티티 생성 ==> order 엔티티. orderItems(List)에 orderItem 엔티티 추가.
        Order order = Order.createOrder(member, orderItemList);

        //5. order 엔티티 저장 ==> orderItem 엔티티 저장
        orderRepository.save(order);

        return order.getId();
    }

    //특정 회원의 주문 내역을 조회하고, 주문별 상세 상품 정보를 대표 이미지 포함하여 OrderHistDto로 변환서 페이징 처리된 결과를 반환한다.
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        // 해당 회원(email)의 주문 목록을 페이징 조건(pageable)에 맞게 조회
        List<Order> orders = orderRepository.findOrders(email, pageable);
        // 해당 회원의 전체 주문 개수를 조회 (페이징 totalCount 계산용)
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        // 조회한 주문(Order) 엔티티들을 순회
        for (Order order : orders) {
            // 주문 단위로 DTO 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);

            // 주문에 포함된 상품(주문상품 목록) 조회
            List<OrderItem> orderItems = order.getOrderItems();

            // 주문 상품 각각에 대해 DTO 변환
            for (OrderItem orderItem : orderItems) {
                // 해당 상품의 대표 이미지 조회 (repImgYn = "Y")
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");

                // 주문상품 DTO 생성 (상품 + 대표 이미지 URL)
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());

                // 생성한 주문상품 DTO를 주문 DTO에 추가
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            // 최종 주문 DTO 리스트에 추가
            orderHistDtos.add(orderHistDto);
        }

        // 페이징 처리된 주문 DTO 리스트를 반환
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }
}
