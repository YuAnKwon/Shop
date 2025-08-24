package com.shop.repository.order;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.shop.entity.QOrder.order;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Order> findOrders(String email, Pageable pageable) {
        /* SELECT *
        FROM orders o
        INNER JOIN member m
        ON o.member_id = m.member_id
        WHERE m.email = ?
        ORDER BY o.order_date DESC

        EMAIL이 ?인 사람의 주문정보와 회원정보를 최신순으로 조회 */
        return jpaQueryFactory
                .select(order)                       // 조회 대상: Order 엔티티
                .from(order)                         // 조회 시작 테이블: Order
                .innerJoin(order.member)             // Order와 Member 조인 (FK 관계)
                .where(order.member.email.eq(email)) // 조건: 회원의 이메일이 파라미터 email과 같은 경우
                .orderBy(order.orderDate.desc())     // 정렬: 주문일자를 기준으로 최신순 내림차순
                .offset(pageable.getOffset())        // 페이징: 몇 번째 데이터부터 가져올지 (시작 위치)
                .limit(pageable.getPageSize())       // 페이징: 한 번에 가져올 데이터 개수 (페이지 크기)
                .fetch();
    }

    @Override
    public Long countOrder(String email) {
        /* 
        SELECT COUNT(*)
        FROM orders o
        INNER JOIN member m
        ON o.member_id = m.member_id
        WHERE m.email = ?  */

       Long total = jpaQueryFactory
                .select(Wildcard.count)
                .from(order)
                .innerJoin(order.member)
                .where(order.member.email.eq(email))
                .fetchOne();

        Optional<Long> totalCount = Optional.ofNullable(total);
        return totalCount.orElse(0L);
    }
}
