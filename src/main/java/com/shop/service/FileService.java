package com.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    /*
    <<args>>
    uploadPath : 파일을 저장할 위치(파일 시스템)
    originalFileName : 파일 원래 이름
    fileData : 파일의 바이너리 데이터
     */
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
        //UUID : 서로다른 개체들을 구별하기 위해서 이름을 부여할때. 파일 중복 문제 해결.
        UUID uuid = UUID.randomUUID(); //새로만든 파일명
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); //확장자 출력
        String savedFileName = uuid.toString() + extension; // 새파일명+확장자

        // 업로드할 파일의 전체 경로를 생성 (저장 경로 + 파일명)
        // 예: uploadPath = "C:/shop/item", savedFileName = "abc123.png"
        // → fileUploadFullUrl = "C:/shop/item/abc123.png"
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        log.info("방금 저장된 파일 명 : {}", fileUploadFullUrl);
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl); // 지정한 경로에 파일을 쓰기 위한 출력 스트림 생성
        fos.write(fileData); // 업로드된 파일의 바이트 데이터를 실제 파일로 저장
        fos.close(); // 파일 쓰기가 끝났으니 출력 스트림을 닫아 리소스 해제
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception {
        // filePath(삭제할 파일의 경로)를 이용하여 File 객체 생성
        // 예: filePath = "C:/shop/item/abc123.png"
        File deletFile = new File(filePath);

        if (deletFile.exists()) {
            deletFile.delete();
            log.info("[파일 삭제] {}", filePath);
        } else {
            log.info("[존재하지 않는 파일] {}", filePath);
        }
    }
}
