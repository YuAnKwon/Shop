package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity{
    @Id
    @Column(name="cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // member테이블의 member_id(PK)를 참조하는 cart테이블의 member_id(FK)
    // @JoinColumn() 어노테이션에 들어가는 name => FK 컬럼 이름 = 부모 테이블의 PK 컬럼 이름
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", unique = true)
    private Member member;

    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}
