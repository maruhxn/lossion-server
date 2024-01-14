package com.maruhxn.lossion.domain.topic.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UpdateTopicReq {
    private Long categoryId;
    private String title;
    private String description;
    private List<MultipartFile> images;
    private String firstChoice;
    private String secondChoice;
    private LocalDateTime closedAt;

    @Builder
    public UpdateTopicReq(Long categoryId, String title, String description, List<MultipartFile> images, String firstChoice, String secondChoice, LocalDateTime closedAt) {
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.images = images;
        this.firstChoice = firstChoice;
        this.secondChoice = secondChoice;
        this.closedAt = closedAt;
    }
}
