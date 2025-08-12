package com.shop.entity;

import com.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //order 키워드가 있어서 orders로 !!
@Getter
@Setter
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // 1:n -> order:orderItem
    // 컬럼이 아니고, 다른 테이블과의 관계를 표현하는 ‘읽기 전용’ 연관관계 매핑 필드.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // 관계의 주인-> OrderItem의 필드명임. 컬럼명 아님 !
    private List<OrderItem> orderItems = new ArrayList<>(); //OrderItem의 private Order order임.

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태

    private LocalDateTime regTime; //데이터가 언제 처음 만들어졌는지 알기 위해

    private LocalDateTime updateTime; //데이터가 마지막으로 언제 수정됐는지 추적하기 위해


}
