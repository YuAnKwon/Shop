package com.shop.repository.cartItem;

import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemRepositoryCustom {
    CartItem findByCartAndItem(Cart cart, Item item);
}
