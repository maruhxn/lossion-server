package com.maruhxn.lossion.domain.topic.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CreateTopicReq {
    @NotEmpty(message = "제목을 입력해주세요.")
    @Size(min = 2, max = 255, message = "제목은 2 ~ 255 글자입니다.")
    private String title;

    @NotEmpty(message = "내용을 입력해주세요.")
    private String description;

    @NotEmpty(message = "1번 선택지를 입력해주세요.")
    @Size(max = 255, message = "선택지는 최대 255글자입니다.")
    private String firstChoice;

    @NotEmpty(message = "2번 선택지를 입력해주세요.")
    @Size(max = 255, message = "선택지는 최대 255글자입니다.")
    private String secondChoice;

    @NotNull(message = "토론 종료 시각을 입력해주세요.")
    private LocalDateTime closedAt;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    private List<MultipartFile> images;

    @Builder
    public CreateTopicReq(String title, String description, String firstChoice, String secondChoice, LocalDateTime closedAt, Long categoryId, List<MultipartFile> images) {
        this.title = title;
        this.description = description;
        this.firstChoice = firstChoice;
        this.secondChoice = secondChoice;
        this.closedAt = closedAt;
        this.categoryId = categoryId;
        this.images = images;
    }
}
