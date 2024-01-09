package com.maruhxn.lossion.infra;

import com.maruhxn.lossion.domain.topic.domain.TopicImage;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    @Value("${file.upload_dir}")
    private String fileDir;

    private final Environment environment;

    public TopicImage storeOneFile(MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException(ErrorCode.EMPTY_FILE);

        String originalFilename = file.getOriginalFilename(); // 파일 원본 이름
        String storeFileName = createStoreFileName(originalFilename); // 서버에 저장된 파일 이름 (랜덤)
        String savePath = getFullPath(storeFileName); // 서버에 저장된 경로

        if (environment.acceptsProfiles(Profiles.of("test"))) {
            return TopicImage.builder()
                    .originalName(originalFilename)
                    .storedName(storeFileName)
                    .build();
        }

        try {
            file.transferTo(new File(savePath)); // 파일 저장
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.INTERNAL_ERROR, e);
        }

        return TopicImage.builder()
                .originalName(originalFilename)
                .storedName(storeFileName)
                .build();
    }

    @Override
    public String saveAndExtractUpdatedProfileImage(MultipartFile profileImage) {
        if (profileImage.isEmpty()) throw new BadRequestException(ErrorCode.EMPTY_FILE);

        String originalFilename = profileImage.getOriginalFilename(); // 파일 원본 이름
        String storeFileName = createStoreFileName(originalFilename); // 서버에 저장된 파일 이름 (랜덤)
        String savePath = getFullPath(storeFileName); // 서버에 저장된 경로
        try {
            profileImage.transferTo(new File(savePath)); // 파일 저장
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.INTERNAL_ERROR, e);
        }

        return storeFileName;
    }

    public List<TopicImage> storeFiles(List<MultipartFile> files) {
        List<TopicImage> storedFileURLResult = new ArrayList<>();
        files.forEach(multipartFile ->
                storedFileURLResult.add(storeOneFile(multipartFile)));
        return storedFileURLResult;
    }

    /**
     * 이미지 조회
     *
     * @param fileName
     * @return
     */
    @Override
    public Resource getImage(String fileName) {
        Resource resource = null;

        try {
            resource = new UrlResource("file:" + getFullPath(fileName));
        } catch (Exception e) {
            throw new InternalServerException(ErrorCode.INTERNAL_ERROR, e);
        }
        return resource;
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
     * @param storeFileName
     */
    @Override
    public void deleteFile(String storeFileName) {
        String savePath = getFullPath(storeFileName); // 서버에 저장된 경로
        try {
            File file = new File(savePath);
            file.delete();
        } catch (Exception e) {
            throw new InternalServerException(ErrorCode.INTERNAL_ERROR, e);
        }
    }
}
