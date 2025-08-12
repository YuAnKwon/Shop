package com.shop.entity;

import com.shop.dto.MemberFormDto;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional //메서드 끝날 때 트랜잭션이 자동으로 flush + commit 해줌
@TestPropertySource(locations = "classpath:application-test.properties")
class CartTest {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CartRepository cartRepository;
    @PersistenceContext
    EntityManager em;

    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("창원시");
        memberFormDto.setPassword("123456");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){
        // 1. 신규 회원 등록
        Member member = createMember();
        memberRepository.save(member); //save는 바로 저장.

        // 2. 해당 회원 엔티티의 장바구니 생성 및 등록
        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        /* JPA는 기본적으로 **영속성 컨텍스트(Persistence Context)**라는 1차 캐시를 사용합니다.
        save() 후에 같은 트랜잭션 내에서 findById()를 하면, DB가 아니라 1차 캐시에서 꺼내옵니다.
        그래서 DB에 실제 쿼리가 안 나갈 수 있습니다. */

        System.out.println("저장 하기 전 cart의 주소값 : "+ cart.hashCode());
        System.out.println("저장 하기 전 member의 주소값 : "+ member.hashCode());

        em.flush(); // 현재까지의 변경 사항 DB에 즉시 반영
        em.clear(); // 1차 캐시를 비움. 영속성 컨텍스트에 엔티티가 없을 경우 db를 조회함.

        // 3. 저장된 장바구니를 통해 해당 회원 조회
        // Optional 안에 값이 존재하면 꺼내서 받고, null이면 Exception 던지기
        Optional<Cart> savedCartOp = cartRepository.findById(cart.getId()); // clear()를 안 하면, 이 시점에서 DB 조회 안 하고 1차 캐시에서 가져옴
        Cart savedCart = savedCartOp.orElseThrow(EntityNotFoundException::new);

        // 4. 조회된 회원과 저장한 회원 정보가 일치하는지 확인.
        Member foundMember = savedCart.getMember();

        System.out.println("저장한 후 cart의 주소값 : "+ savedCart.hashCode());
        System.out.println("저장한 후 member의 주소값 : "+ foundMember.hashCode());

        assertEquals(savedCart.getMember().getId(), member.getId());

    }
}