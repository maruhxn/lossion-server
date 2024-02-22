package com.maruhxn.lossion.infra.api;

import com.maruhxn.lossion.global.common.Constants;
import com.maruhxn.lossion.infra.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping(value = "/{fileName}", produces = "image/jpeg")
    public Resource getFile(@PathVariable String fileName) {
        if (fileName.equals(Constants.BASIC_PROFILE_IMAGE_NAME)) {
            return new ClassPathResource("static/img/" + fileName);
        }
        return fileService.getImage(fileName);
    }

}
