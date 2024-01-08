package com.maruhxn.lossion.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class FolderCreationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Specify the folder path (you can customize this)
        String folderPath = "upload";

        // Create the folder if it doesn't exist
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                log.info("Folder created successfully: {}" + folder.getAbsolutePath());
            } else {
                log.error("Failed to create folder: {}" + folder.getAbsolutePath());
            }
        } else {
            log.info("Folder already exists: {}" + folder.getAbsolutePath());
        }
    }
}