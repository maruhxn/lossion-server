package com.maruhxn.lossion.domain.topic.dto.response;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopicItem {
    private Long topicId;
    private CategoryItem categoryItem;
    private String title;
    private Long viewCount;
    private AuthorInfoItem author;
    private Long commentCount;
    private Long favoriteCount;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Boolean isClosed;

    @Builder
    @QueryProjection
    public TopicItem(Long topicId, Category category, String title, Long viewCount, Member author, Long commentCount, Long favoriteCount, LocalDateTime createdAt, LocalDateTime closedAt, Boolean isClosed) {
        this.topicId = topicId;
        this.categoryItem = CategoryItem.from(category);
        this.title = title;
        this.viewCount = viewCount;
        this.author = AuthorInfoItem.from(author);
        this.commentCount = commentCount;
        this.favoriteCount = favoriteCount;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.isClosed = isClosed;
    }
}
