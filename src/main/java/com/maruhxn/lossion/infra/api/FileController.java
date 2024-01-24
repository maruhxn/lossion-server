package com.maruhxn.lossion.infra.api;

import com.maruhxn.lossion.infra.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping(value = "/{fileName}")
    public Resource getFile(@PathVariable String fileName) {
        return fileService.getImage(fileName);
    }

}
