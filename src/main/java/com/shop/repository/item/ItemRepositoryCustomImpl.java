package com.shop.repository.item;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.shop.entity.QItem.item;
import static com.shop.entity.QItemImg.itemImg;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory; // 동적으로 쿼리작성할려고 클래스 사용.

    private BooleanExpression regDtsAfter(String searchDateType){
        //searchDateType 화면 => all, 1d, 1w, 1m, 6m

        LocalDateTime now = LocalDateTime.now();

        if(StringUtils.equals(searchDateType, "1d")){
            now = now.minusDays(1); //지금으로부터 하루전
        } else if(StringUtils.equals(searchDateType,"1w")){
            now = now.minusWeeks(1); //한주전
        } else if(StringUtils.equals(searchDateType,"1m")){
            now = now.minusMonths(1); //한달전
        } else if(StringUtils.equals(searchDateType,"6m")){
            now = now.minusMonths(6); //6개월 전
        } else if(StringUtils.equals(searchDateType, "all") || searchDateType == null){
            // 전부 조회, 날짜 지정을 안하면 전체조회
            return null;
        }

        return item.regTime.after(now);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        // 상품 판매 상태 조건이 null일경우는 null (where절에서 무시됨), 아니면 해당 조건의 상품만 조회.
        if(searchSellStatus == null){
            return null;
        }
        return item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        // 무엇을 검색?(searchBy), 검색키워드(searchQuery)
        // searchBy : "itemNm", "createdBy"

        if(StringUtils.equals(searchBy, "itemNm")){
            return item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals(searchQuery, "createdBy")){
            return item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        /*
        목적 : item 테이블에서 검색 조건에 맞는 결과를 페이지 단위로 조회

        조건1 : searchDateType에 따라 검색 기간 설정
        조건2 : searchSellStatus에 따라 상품 판매 상태 설정. (SOLD_OUT, SELL)
        조건3 : searchBy + createdBy에 따라 검색 키워드 설정.
        ==> item_id를 기준으로 내림차순, pageable 기준에 따른 페이징 결과 반환.

        SELECT * FROM item
        WHERE 조건1 AND 조건2 AND 조건3
        ORDER BY item_id DESC
        LIMIT, OFFSET . . .
        */

        /*
        Page(인터페이스) - PageImpl(구현체)
        PageImpl
        ㄴcontent : List<T>
        ㄴtotalCount : 페이지 총 개수
        ㄴnumber : 페이지번호
        */

        //content에 들어갈 List<Item>
        List<Item> content = jpaQueryFactory.selectFrom(item) //상품 데이터 조회하기 위해 QItem의 item
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                ) //where(조건1, 조건2, 조건3)
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(); // 다중행 출력.

        // 반환될 리스트 개수
        /*
        SELECT COUNT(*)
        FROM item
        WHERE 조건1 AND 조건2 AND 조건3
        */

        // content.size하면 한 페이지당 상품 개수임. 전체 개수가 아님.
        // 전체 개수 반환.
        Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne(); // 하나의 행만 출력됨.

        /* Optional.ofNullable(totalCount) →
        totalCount가 null이면 → Optional.empty()
        totalCount가 null이 아니면 → Optional.of(totalCount)
        결과: Optional<Long> 타입 객체로 감싸서 null 안전하게 처리 */
        Optional<Long> total = Optional.ofNullable(totalCount); //null이 될수있으므로 (검색된 상품이 0개)


        /* Spring Data JPA의 PageImpl로 감싸면 편리하게 페이지 처리 가능
        Pageable : 페이징과 정렬 정보를 담는 인터페이스,
        pageable.getOffset() → 조회 시작 위치 (예: 0, 10, 20…).  pageable.getPageSize() → 한 페이지 데이터 개수
        */
        return new PageImpl<>(content, pageable, total.orElse(0L)); //null이면 0으로
        //totalCount
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<MainItemDto> content = jpaQueryFactory
                .select(Projections.fields(MainItemDto.class,
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        item.price,
                        itemImg.imgUrl)
                )
                .from(itemImg)
                .innerJoin(itemImg.item) //itemImg과 item 을 조인한다.
                .where(itemImg.repimgYn.eq("Y"))
                .where(searchByLike("itemNm", itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .innerJoin(itemImg.item) //itemImg과 item 을 조인한다.
                .where(itemImg.repimgYn.eq("Y"))
                .where(searchByLike("itemNm", itemSearchDto.getSearchQuery()))
                .fetchOne();

        Optional<Long> total = Optional.ofNullable(totalCount);
        return new PageImpl<>(content, pageable, total.orElse(0L));
    }
}
