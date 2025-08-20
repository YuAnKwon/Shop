package com.shop.repository.cartItem;

import com.shop.dto.CartDetailDto;

import java.util.List;

public interface CartItemRepositoryCustom {

    /*
    SELECT ci.id, i.item_name, i.item_price, ci.count, im.imgUrl
    FROM cart_item ci
    JOIN item i
        ON (ci.item_id = i.item_id)
    JOIN item_img im
        ON (i.item_id = im.item_id)
    WHERE ci.cart_id = ?
        AND im.repimg_yn = "Y"
    ORDER BY ci.reg_time DESC
    */
    List<CartDetailDto> findCartDetailDtoList(Long cartId);
}
