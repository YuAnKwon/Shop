package com.shop.repository.cartItem;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.dto.CartDetailDto;
import lombok.RequiredArgsConstructor;

import java.util.List;
import static com.shop.entity.QCartItem.cartItem;
import static com.shop.entity.QItem.item;
import static com.shop.entity.QItemImg.itemImg;

@RequiredArgsConstructor
public class CartItemRepositoryCustomImpl implements CartItemRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CartDetailDto> findCartDetailDtoList(Long cartId) {
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

        return jpaQueryFactory
                .select(Projections.fields(CartDetailDto.class,
                        cartItem.id.as("cartItemId"),
                        item.itemNm,
                        item.price,
                        cartItem.count,
                        itemImg.imgUrl))
                .from(item)
                .join(cartItem)
                .on(cartItem.item.eq(item))
                .join(itemImg)
                .on(itemImg.item.eq(item))
                .where(cartItem.cart.id.eq(cartId))
                .where(itemImg.repimgYn.eq("Y"))
                .orderBy(cartItem.regTime.desc())
                .fetch();
    }
}
