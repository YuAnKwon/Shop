package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart")
    public ResponseEntity<?> order(@RequestBody @Valid CartItemDto cartItemDto,
                                   BindingResult bindingResult, Authentication authentication){

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append("\n");
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        Long cartItemId;
        try{
            cartItemId = cartService.addCart(cartItemDto, authentication.getName());

        } catch (EntityNotFoundException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(cartItemId);

    }

    @GetMapping("/cart")
    public String cartList(Authentication authentication, Model model){
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(authentication.getName());
        model.addAttribute("cartItems", cartDetailDtoList);
        return "cart/cartList";
    }

    @PatchMapping("/cartItem/{cartItemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId, int count){
        cartService.updateCartItemCount(cartItemId, count);
        return ResponseEntity.ok().body(cartItemId);
    }

    @DeleteMapping("/cartItem/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemId){
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok().body(cartItemId);
    }

    @PostMapping("/cart/orders")
    public ResponseEntity<?> orders(@RequestBody CartOrderDto cartOrderDto, Authentication authentication){
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        if(cartOrderDtoList.isEmpty()){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }
        Long orderId = cartService.orderCartItems(cartOrderDtoList, authentication.getName());
        return ResponseEntity.ok().body(orderId);
    }
}
