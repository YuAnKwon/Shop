package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;
    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    // 1. MultipartFile에 들어있는 itemImgFile을 파일시스템에 저장 -> FileService 호출.
    // 2. DB에 저장할 ItemImg 정보 ==> DB에 저장 ==> ItemImgRepository
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename(); // 업로드한 파일의 원본 이름 (사용자가 업로드한 파일명)
        String imgName = "";  // 서버에 저장할 파일명 (UUID + 확장자 등으로 저장됨)
        String imgUrl = "";   // 클라이언트가 접근할 이미지 URL 경로 (웹에서 이미지 보여줄 때 사용)

        // 원본 파일명이 비어있지 않은 경우에만 파일 업로드 수행

        if (!StringUtils.isEmpty(oriImgName)) {
            // FileService의 uploadFile 메서드를 호출해 파일 시스템에 저장하고, 저장된 파일명을 반환받음 (예: "uuid.png")
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            // 웹에서 이미지를 요청할 수 있도록 URL 경로 생성 (예: "/images/item/uuid.png")
            imgUrl = "/images/item/" + imgName;
        }

        // ItemImg 엔티티 객체에 원본 이름, 저장된 파일명, 이미지 URL 정보를 세팅
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        //DB에 저장된 파일 정보 저장 . . .2
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile)throws Exception{
        if(!itemImgFile.isEmpty()){
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityExistsException::new);

            // 비어있지 않을때,
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){
                //기존에 파일시스템에 저장되어있던 이미지 삭제
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }
            //2. DB에 기존 레코드 내용 업데이트
            // Jpa Entity update ==> dirty checking => 조회된 엔티티의 필드변경.
            String oriImgName = itemImgFile.getOriginalFilename();
            // 새 파일 저장하고 저장된 파일명 받기
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }
}
