package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter
@Setter
public class OrderItem extends BaseEntity {
    @Id
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer orderPrice; //주문가격

    private Integer count; //수량

    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item); //주문할 상품
        orderItem.setCount(count); //상품 개수
        orderItem.setOrderPrice(item.getPrice());

        item.removeStock(count); //주문수량만큼 재고수량 감소
        return orderItem;
    }
    public int getTotalPrice(){
        return orderPrice * count;
    }

    // 주문취소 시 주문 수량만큼 재고를 더해준다.
    public void cancel(){
        this.getItem().addStock(count);
    }
}
