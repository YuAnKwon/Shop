package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.repository.item.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.order.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderTest {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @PersistenceContext
    EntityManager em;

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10_000);
        item.setItemDetail("상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    public Order createOrder(){
        Order order = new Order();
        for(int i=0; i<3; i++){
            Item item = createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setOrder(order);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1_000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){ // 부모 엔티티 저장 시 자식 엔티티도 함께 저장되는지 확인
        // 1. 주문 엔티티 생성
        Order order = new Order();

        for(int i=0; i<3; i++){
            // 2. item 엔티티 저장
            Item item = createItem();
            itemRepository.save(item);

            // 3. 위에서 저장된 item으로 order_item(주문과 특정 아이템을 연결해주는 역할)
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setOrder(order);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1_000);
            // 현재 Order는 저장이 안됨, order 엔티티의 orderItems 리스트에 orderItem 엔티티를추가.
            order.getOrderItems().add(orderItem);
        }

        // order는 저장 전에 orderItem 엔티티를 3개 포함한 상태.
        // orderItem 말고 order를 저장함.
        orderRepository.saveAndFlush(order); //save 함수와는 다르게 바로 DB에 저장을 요청하는 함수
        em.clear();

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());

    }

    @Test
    @DisplayName("고아객체 제거 테스트") //부모 컬렉션에서 자식을 제거하면 DB에서도 삭제되는지 확인
    public void orphanRemovalTest(){
        Order order= createOrder();
        order.getOrderItems().remove(0);  // 부모-자식 관계 끊기
        em.flush(); // orphanRemoval=true라면 DELETE 쿼리 실행
    }

    /* 프록시
    - JPA에서 지연 로딩을 설정하면, 부모나 자식 엔티티를 처음부터 DB에서 조회하지 않고 대신 프록시 객체를 넣어둔다.
    그리고 엔티티의 데이터를 처음 접근할 때(getter 호출) DB 쿼리를 날려서 진짜 데이터를 가져온다.
    - 처음에는 진짜 데이터 없이 객체 껍데기만 생성. 실제로 필요한 시점(예: getter 호출) → 그때 DB에서 SELECT 쿼리 실행
    * */
    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = createOrder(); //Order와 그 안의 OrderItem들을 저장 (Order 1개, 그 안에 OrderItem 3개, 각각의 OrderItem이 참조하는 Item 3개,Member 1개)
        Long orderItemId = order.getOrderItems().get(0).getId(); //첫 번째 OrderItem의 PK를 가져옴
        // Order 데이터가 있는 게 아니라, Hibernate가 만든 프록시 객체

        em.flush(); //영속성 컨텍스트(1차 캐시)에 있는 변경 내용을 DB에 반영
        em.clear(); //영속성 컨텍스트 초기화. 1차 캐시에 이미 있는 엔티티는 DB 조회 없이 바로 꺼내오니까, 지연 로딩 테스트가 무의미해짐

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class : "+ orderItem.getOrder().getClass()); // Order 타입이 아니라 Hibernate가 만든 Order의 자식 클래스(프록시)
        // 즉시 로딩일땐 : class com.shop.entity.Order
        System.out.println("================================================================");
        orderItem.getOrder().getOrderDate(); //프록시 객체의 실제 데이터 접근 시점. JPA는 이 순간 DB에 쿼리 날려서 진짜 Order 엔티티를 로딩함.
        // 이후 프록시가 실제 엔티티로 바뀌어 데이터 사용 가능.
        System.out.println("================================================================");
        orderItem.getItem().getItemDetail();

    }
}