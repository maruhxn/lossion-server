package com.maruhxn.lossion.infra.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.maruhxn.lossion.domain.topic.domain.TopicImage;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.global.error.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileService implements FileService {

    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String saveAndExtractUpdatedProfileImage(MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException(ErrorCode.EMPTY_FILE);

        String fileName = file.getOriginalFilename();
        String storeFileName = createStoreFileName(fileName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            s3Client.putObject(new PutObjectRequest(bucket, storeFileName, file.getInputStream(), metadata));
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.S3_UPLOAD_ERROR, e);
        }
        return storeFileName;
    }

    @Override
    public Resource getImage(String storeFileName) {
        URL fileUrl = s3Client.getUrl(bucket, storeFileName);
        return new UrlResource(fileUrl);
    }

    @Override
    public void deleteFile(String storeFileName) {
        boolean isExist = s3Client.doesObjectExist(bucket, storeFileName);

        if (!isExist) {
            throw new EntityNotFoundException(ErrorCode.NOT_FOUND_FILE);
        }

        s3Client.deleteObject(bucket, storeFileName);
    }

    @Override
    public TopicImage storeOneFile(MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException(ErrorCode.EMPTY_FILE);

        String fileName = file.getOriginalFilename();
        String storeFileName = createStoreFileName(fileName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            s3Client.putObject(new PutObjectRequest(bucket, storeFileName, file.getInputStream(), metadata));
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.S3_UPLOAD_ERROR, e);
        }

        return TopicImage.builder()
                .originalName(fileName)
                .storedName(storeFileName)
                .build();
    }

    @Override
    public List<TopicImage> storeFiles(List<MultipartFile> files) {
        List<TopicImage> storedFileURLResult = new ArrayList<>();
        files.forEach(multipartFile ->
                storedFileURLResult.add(storeOneFile(multipartFile)));
        return storedFileURLResult;
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
}
