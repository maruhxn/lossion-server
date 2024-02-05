package com.maruhxn.lossion.domain.topic.dto.response;

import com.maruhxn.lossion.domain.topic.domain.Category;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class MyTopicItem {
    private Long topicId;
    private CategoryItem categoryItem;
    private String title;
    private Long viewCount;
    private Long commentCount;
    private Long favoriteCount;
    private Long voteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    private Boolean isClosed;

    @Builder
    @QueryProjection
    public MyTopicItem(Long topicId, Category category, String title, Long viewCount, Long commentCount, Long favoriteCount, Long voteCount, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime closedAt, Boolean isClosed) {
        this.topicId = topicId;
        this.categoryItem = CategoryItem.from(category);
        this.title = title;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.favoriteCount = favoriteCount;
        this.voteCount = voteCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.closedAt = closedAt;
        this.isClosed = isClosed;
    }


}
