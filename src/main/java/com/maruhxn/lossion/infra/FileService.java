package com.maruhxn.lossion.infra;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    String saveAndExtractUpdatedProfileImage(MultipartFile profileImage);

    Resource getImage(String fileName);

    void deleteFile(String storeFileName);

    void storeOneFile(MultipartFile file);

    void storeFiles(List<MultipartFile> files);
}
