package com.shop.repository;

import com.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
    /* FK인 item_id로 ItemImg를 조회
    SELET * FROM item_img WHERE item_id=?
    ORDER BY item_img_id;
     */
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId); //그냥 FindById하면 PK을 말하기때문에 xx 우린 FK가필요.
    ItemImg findByItemIdAndRepimgYn(Long itemId, String repimgYn);

}
