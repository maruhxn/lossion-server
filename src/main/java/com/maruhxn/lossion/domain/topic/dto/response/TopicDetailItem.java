package com.maruhxn.lossion.domain.topic.dto.response;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.domain.topic.domain.TopicImage;
import com.maruhxn.lossion.domain.topic.domain.Vote;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopicDetailItem {
    private Long topicId;
    private CategoryItem categoryItem;
    private String title;
    private String description;
    private String firstChoice;
    private String secondChoice;
    private AuthorInfoItem author;
    private Long viewCount;
    private Long commentCount;
    private Long favoriteCount;
    private VoteCountInfo voteCountInfo;
    private Boolean isClosed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    private List<TopicImageItem> images;

    @Builder
    @QueryProjection
    public TopicDetailItem(Long topicId, Category category, String title, String description, String firstChoice, String secondChoice, Member author, Long viewCount, Long commentCount, Long favoriteCount, List<Vote> votes, Boolean isClosed, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime closedAt, List<TopicImage> images) {
        this.topicId = topicId;
        this.categoryItem = CategoryItem.from(category);
        this.title = title;
        this.description = description;
        this.firstChoice = firstChoice;
        this.secondChoice = secondChoice;
        this.author = AuthorInfoItem.from(author);
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.favoriteCount = favoriteCount;
        this.voteCountInfo = VoteCountInfo.from(votes);
        this.isClosed = isClosed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.closedAt = closedAt;
        this.images = images.stream()
                .map(TopicImageItem::from)
                .toList();
    }


    public static TopicDetailItem from(Topic topic) {
        return TopicDetailItem.builder()
                .topicId(topic.getId())
                .title(topic.getTitle())
                .description(topic.getDescription())
                .firstChoice(topic.getFirstChoice())
                .secondChoice(topic.getSecondChoice())
                .commentCount((long) topic.getComments().size())
                .viewCount(topic.getViewCount())
                .favoriteCount((long) topic.getFavorites().size())
                .votes(topic.getVotes())
                .images(topic.getImages())
                .isClosed(topic.getIsClosed())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .closedAt(topic.getClosedAt())
                .author(topic.getAuthor())
                .category(topic.getCategory())
                .build();
    }
}
