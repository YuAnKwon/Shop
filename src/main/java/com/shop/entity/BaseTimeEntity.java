package com.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// Auditing 기능의 적용을 받음.
// JPA에서 엔티티의 생성 시간과 수정 시간을 자동으로 관리하기 위해 자주 쓰는 공통 부모 클래스
@EntityListeners(value = {AuditingEntityListener.class}) //Auditing 적용 ->
// JPA가 엔티티 생명주기 이벤트(예: 저장, 수정 등)를 감지할 때, AuditingEntityListener가 자동으로 호출되어 생성일/수정일을 자동으로 세팅

@MappedSuperclass // 이 클래스는 테이블이 아니다!
// 다른 엔티티 클래스들이 상속받을 때, 이 클래스의 필드들을 자식 엔티티의 컬럼으로 포함시킴. 독립적으로 쿼리 대상이 되는 테이블은 아님
// ex) Order가 BaseTimeEntity를 상속하면 orders 테이블에 createdDate, modifiedDate 컬럼이 추가됨
@Getter
@Setter
// 추상클래스 : 이 자체로는 객체로 존재할수없다. (인스턴스로 xx) 독립적인 사용x -> 매핑의 목적밖에 없음.
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false) // 수정 불가능
    private LocalDateTime regTime;
    // 엔티티가 처음 저장될 때 현재 시각 자동 저장.
    // ex) orderRepository.save(order) 시점에 createdDate 자동 입력

    @LastModifiedDate
    private LocalDateTime updateTime;
    // 엔티티가 수정될 때마다 현재시각 자동 갱신.
    // ex) order.setStatus() 후 save()하면 modifiedDate가 갱신됨
}
