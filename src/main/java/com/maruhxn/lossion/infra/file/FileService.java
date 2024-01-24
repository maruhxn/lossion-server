package com.maruhxn.lossion.infra.file;

import com.maruhxn.lossion.domain.topic.domain.TopicImage;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    String saveAndExtractUpdatedProfileImage(MultipartFile profileImage);

    Resource getImage(String fileName);

    void deleteFile(String storeFileName);

    TopicImage storeOneFile(MultipartFile file);

    List<TopicImage> storeFiles(List<MultipartFile> files);
}
