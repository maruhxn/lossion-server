package com.maruhxn.lossion.domain.topic.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopicSearchCond {
    @Size(max = 255, message = "제목 검색은 최대 255글자입니다.")
    private String title;

    private String description;

    @Size(max = 10, message = "유저명 검색은 최대 10글자입니다.")
    private String author;

    @Builder
    public TopicSearchCond(String title, String description, String author) {
        this.title = title;
        this.description = description;
        this.author = author;
    }
}
