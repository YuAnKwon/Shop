package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity // 클래스를 엔티티로 선언
@Table(name = "item") //엔티티와 매핑할 테이블을 지정
@Getter
@Setter
@ToString
public class Item {
    @Id //기본키
    @Column(name = "item_id") // 컬럼명을 안쓰면 필드명과 똑같이 생성.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //상품 코드

    @Column(nullable = false, length = 50) //String필드는 default로 255이다.
    private String itemNm; // db에는 item_nm으로 자동변환된다.

    @Column(nullable = false) // not null
    private Integer price;

    @Column(nullable = false)
    private Integer stockNumber; // 재고수량

    @Lob //LongText // Large Object 큰 용량의 데이터를 저장할 때 사용하는 어노테이션.
    @Column(nullable = false)
    private String itemDetail; // 상품 상세 설명

    @Enumerated(EnumType.STRING) // 문자열로 저장.
    private ItemSellStatus itemSellStatus; // 상품 판매 상태
    
    private LocalDateTime regTime; // 상품 등록 시간
    private LocalDateTime updateTime;; // 상품 수정 시간
    
}
