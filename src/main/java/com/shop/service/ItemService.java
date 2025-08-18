package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.item.ItemRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemImgRepository itemImgRepository;
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;

    // FileService, ItemService, ItemimgService에서 던진 Exception을 또 던진다 (아마 Controller에서 처리할듯)
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 1. 상품 저장
        Item item = itemFormDto.createItem(); // Dto -> Entity
        itemRepository.save(item);

        // 2. 이미지 여러 개 저장 (반복문 돌면서 처리)
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if (i == 0) { //첫번째 이미지일 경우 대표 이미지로.
                itemImg.setRepimgYn("Y");
            } else {
                itemImg.setRepimgYn("N");
            }
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }
        //저장된 item ID를 반환
        return item.getId();
    }

    @Transactional(readOnly = true) //읽기전용. jpa가 더티체킹을 수행하지 않아 성능을 향상시킴.
    public ItemFormDto getItemDtl(Long itemId) {
        // 1. 해당 상품 ID에 연결된 이미지 엔티티 목록을 ID 오름차순으로 조회
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        // 2. 이미지 DTO 리스트를 담을 빈 ArrayList 생성
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        // 3. 조회한 이미지 Entity ->DTO로 변환하여 리스트에 추가
        for (ItemImg itemImg : itemImgList) {
            itemImgDtoList.add(ItemImgDto.of(itemImg)); //Entity ->DTO
        }
        // ====== 이젠 Item 갖고오기 ======
        // 4. 상품 ID로 상품 엔티티 조회 (없으면 EntityExistsException 발생)
        Item item = itemRepository.findById(itemId) //db에서 id로 item찾기.
                .orElseThrow(EntityExistsException::new);

        // 5. 조회한 상품 엔티티를 ItemFormDto로 변환
        ItemFormDto itemFormDto = ItemFormDto.of(item); // entity -> dto

        // 6. 이미지 DTO 리스트를 상품 DTO에 넣기.
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 기존데이터 갖고오기
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new); // 아이디로 db에 저장되어있는거 item에 담기.

        // DTO에 있는 내용으로 다 교체
        item.updateItem(itemFormDto);

        // 아이템 엔티티 정보 반영 -> 이미지 수정 내역 반영
        List<Long> itemImgIds = itemFormDto.getItemImgIds(); // 어떤 엔티티는 1~5, 6~10 일 것.

        // 상품 이미지 업데이트
        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        return item.getId(); // 수정한 item id 반환.
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
}
