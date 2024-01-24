package com.maruhxn.lossion.infra.file;

import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    @Value("${file.upload_dir}")
    private String fileDir;

    public String storeOneFile(MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException(ErrorCode.EMPTY_FILE);

        String originalFilename = file.getOriginalFilename(); // 파일 원본 이름
        String storeFileName = createStoreFileName(originalFilename); // 서버에 저장된 파일 이름 (랜덤)
        String savePath = getFullPath(storeFileName); // 서버에 저장된 경로

        try {
            file.transferTo(new File(savePath)); // 파일 저장
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.INTERNAL_ERROR, e);
        }

        return storeFileName;
    }

    /**
     * 이미지 조회
     *
     * @param storedFileName
     * @return
     */
    @Override
    public Resource getImage(String storedFileName) {
        try {
            return new UrlResource("file:" + getFullPath(storedFileName));
        } catch (Exception e) {
            throw new InternalServerException(ErrorCode.INTERNAL_ERROR, e);
        }
    }

    /**
     * 서버에 저장될 실제 경로 생성
     *
     * @param fileName
     * @return
     */
    private String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    /**
     * 서버에 저장될 파일명 생성
     *
     * @param originalFilename
     * @return
     */
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    /**
     * 파일 확장자 추출
     *
     * @param originalFilename
     * @return
     */
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    /**
     * 실제 물리 이미지 삭제
     *
     * @param storedFileName
     */
    @Override
    public void deleteFile(String storedFileName) {
        String savePath = getFullPath(storedFileName); // 서버에 저장된 경로
        try {
            File file = new File(savePath);
            file.delete();
        } catch (Exception e) {
            throw new InternalServerException(ErrorCode.INTERNAL_ERROR, e);
        }
    }
}
