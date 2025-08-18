package com.shop.repository.item;

import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

// JapRepository를 상속받음. 2개의 제네릭 타입을 사용. 첫번째는 엔티티 타입 클래스, 두번째는 기본키 타입.
public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    // 쿼리메서드
    List<Item> findByItemNm(String itemNm);

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    // SELECT * FROM item WHERE item_nm=? OR item_detail = ?

    List<Item> findByPriceLessThan(Integer price);
    // SELECT * FROM item WHERE price < ?

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    // SELECT * FROM item WHERE price < ? ORDER BY price DESC

    @Query("SELECT i FROM Item i WHERE i.itemDetail LIKE %:itemDetail% ORDER BY i.price DESC")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value = "SELECT * FROM item i WHERE i.item_detail LIKE %:itemDetail% ORDER BY i.price DESC", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

}
