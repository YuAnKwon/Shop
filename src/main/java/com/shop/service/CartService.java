package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.cartItem.CartItemRepository;
import com.shop.repository.item.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderSevice orderSevice;

    public Long addCart(CartItemDto cartItemDto, String email){
        // 1. Item 엔티티 조회.
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        // 2. Member 엔티티 조회(누구의것)
        Member member = memberRepository.findByEmail(email);

        // 3. Cart 엔티티 조회.
        Cart cart = cartRepository.findByMember(member);
        if(cart == null){
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 4. CartItem 엔티티 조회.
        CartItem savedCartItem = cartItemRepository.findByCartAndItem(cart,item);
        // 4-1. 이미 그 item이 존재하면 => 조회된 cartItem 엔티티 count 증가.
        if(savedCartItem !=null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            // 4-2. 없다 => CartItem 엔티티 저장.
            CartItem newCartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(newCartItem);
            return newCartItem.getId();
        }
    }

    public List<CartDetailDto> getCartList(String email){
        // 1. memeber 조회
        Member member = memberRepository.findByEmail(email);

        // 2. cart 조회 (아무값도 없으면 빈값 반환)
        Cart cart = cartRepository.findByMember(member);
        if(cart == null){
            return new ArrayList<>();
        }

        // 3. cartItem 조회 => 특정 cart에 담긴 CartItem리스트 조회
        List<CartDetailDto> cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    public void updateCartItemCount(Long cartItemId, Integer count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId){
        // #1
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);

        // #2
//        cartItemRepository.deleteById(cartItemId);
    }


    public Long orderCartItems(List<CartOrderDto> cartOrderDtoList, String email) {
        // 책임1. CartItemDto에 있는 cartItemId를 이용 ==> OrderDtoList생성.
        List<OrderDto> orderDtoList = new ArrayList<>();

        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            OrderDto orderDto = OrderDto.builder()
                    .itemId(cartItem.getItem().getId())
                    .count(cartItem.getCount())
                    .build();
            orderDtoList.add(orderDto);
        }
        // 주문 => 수행 => orderService => OrderDtoList, email
        Long orderId = orderSevice.orders(orderDtoList, email);
        // 책임2. 주문이 완료된 cartItemList를 CartItem 테이블에서 삭제한다.
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            cartItemRepository.deleteById(cartOrderDto.getCartItemId());
        }
        return orderId;
    }

}
