package com.maruhxn.lossion.domain.topic.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopicFilterCond {

    private String createdAt;
    private String closedAt;
    private String isClosed;
    private Long favoriteCount;
    private Long commentCount;

    @Builder
    public TopicFilterCond(String createdAt, String closedAt, String isClosed, Long favoriteCount, Long commentCount) {
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.isClosed = isClosed;
        this.favoriteCount = favoriteCount;
        this.commentCount = commentCount;
    }
}
