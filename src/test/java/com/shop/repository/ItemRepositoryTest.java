package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import static com.shop.entity.QItem.item; // 필드를 바로 import한다. (static이라서 가능)

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest // 통합 테스트를 위해. 모든 Bean을 IoC 컨테이너에 등록.
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemRepositoryTest {

    @PersistenceContext //영속성 컨텍스트 사용. 의존성 주입. ENtityManager 빈 주입
    EntityManager em;

    @Autowired
    ItemRepository itemRepository;  // bean 주입

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        // DB에 값 저장 (INSERT)하기
        // 1. 엔티티(ENTITY) 객체를 만든다.
        // 2. 엔티티 객체에 저장하고싶은 값을 담는다.
        // 3. JPA repository를 이용해 저장(save ==> persist + flush) 한다.

        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10_000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest() {
        createDummyItems();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품5");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    public void createDummyItems() {
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10_000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100 + i);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 OR 테스트")
    public void findByItemNmOrItemDetail() {
        createDummyItems();
        createDummyItems();
        List<Item> items = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for (Item item : items) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThan() {
        createDummyItems();
        List<Item> items = itemRepository.findByPriceLessThan(10005);
        for (Item item : items) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc() {
        createDummyItems();
        List<Item> items = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for (Item item : items) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetail() {
        createDummyItems();
        List<Item> items = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for (Item item : items) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemDetailByNative() {
        createDummyItems();
        List<Item> items = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for (Item item : items) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queyrDslTest() {
        createDummyItems();
        /* SELECT * FROM item
           WHERE item_sel_status = sell
               AND item_detail LIKE %테스트 상품 상세 설명%
           ORDER BY price DESC */
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); // QueryDSL 쿼리를 만들기 위한 팩토리 객체 생성
        JPAQuery<Item> query = queryFactory.selectFrom(item) // 자바지만 SQL과 비슷하게 소스 작성 --> 타입안정성
                .where(item.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(item.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(item.price.desc());

        List<Item> items = query.fetch(); //조회 결과 반환
        // 하나가 올경우 fetchOne, 1건만 반환하고싶으면 firstFirst()

        for (Item item : items) {
            System.out.println(item.toString());
        }
    }

    public void createDummyItems2() {
        //sell
        for (int i = 1; i <= 5; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10_000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100 + i);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        // sold_out
        for (int i = 6; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10_000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트2")
    // WHERE이 동적. 조건문을 써서 필요한 조건만 동적으로 쿼리에 넣는 것이 BooleanBuilder와 동적 쿼리의 핵심.
    public void queryDslTest2() {
        // 1. 더미 데이터 생성
        createDummyItems2();

        // 2. 사용자 검색 조건 설정 (가정)
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10_003;
        String itemSellStatus = "SELL"; // 문자열로 넘어왔다고 치자.
        int pageNum = 1; //

        // 3. 페이지 정보 생성.
        Pageable pageable = PageRequest.of(pageNum - 1, 5); // LIMIT 5 OFFSET pageNum-1

        /*
        조건1. 주어진 itemDetail 키워드를 포함
        조건2. 상품가격이 주어진 price보다 커아햠.
        조건3. 조회하려는 상태가 SELL인 경우 상품의 판매 상태가 SELL이고, 아니면 제외
        조건4. 5개씩 페이징된 데이터를 조회.
        */

        // 4. QueryDSL 기반 쿼리 빌드 시작
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<Item> baseQuery = queryFactory.selectFrom(item);
        // SELECT * FROM item 이까지 된거임.

        //5. 조건 조합 - BooleanBuilder로 동적 WHERE 구성
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%")); // 이 조건을 포함하는지.
        booleanBuilder.and(item.price.gt(price));

        // itemSellStatus가 "SELL"일 경우에만 아래 조건을 추가
        if (itemSellStatus.equals("SELL")) {
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
            // 만약 itemSellStatus가 "SOLD_OUT"이면 이 조건은 추가되지 않아 판매 상태 조건 없이 조회됨
        }
        // 위에서 조합한 조건들을 QueryDSL의 where 절에 적용
        //JPAQuery<Item> query2 = query.where(booleanBuilder);
        /*
        SELECT * FROM item
        WHERE itemDetail LIKE ?
        AND price > ?
        AND item_sell_status = "SELL" <<-- 조건부
        */

        // 6. 조건 적용 + 정렬 + 페이징
        JPAQuery<Item> conditionedQuery = baseQuery.where(booleanBuilder);
        JPAQuery<Item> pagedQuery = conditionedQuery
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        /*
        SELECT * FROM item
        WHERE itemDetail LIKE ?
        AND price > ?
        AND item_sell_status = "SELL" <<-- 조건부
        ORDER BY id DESC
        LIMIT 5 OFFSET ?
        */

        // 7. 실제 데이터 fetch
        List<Item> contents = pagedQuery.fetch();

        // 8. count조건 포함해서 따로 조회
        Long totalCount = queryFactory.select(Wildcard.count)
                .from(item)
                .where(booleanBuilder)
                .fetchOne();
        // select count(*) from item

        // 9. Page 객체로 감싸기
        Page<Item> result = new PageImpl<>(contents, pageable, totalCount);

        // 10. 결과 출력
        System.out.println("총 컨텐츠 요소의 개수 :" + result.getTotalElements());
        System.out.println("조회가능한 총 페이지 수 :" + result.getTotalPages());

        List<Item> items = result.getContent();

        for (Item item : items) {
            System.out.println(item);
        }
    }
}
