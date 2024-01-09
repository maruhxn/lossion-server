package com.maruhxn.lossion.domain.topic.dto.response;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
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
    private Long voteCount;
    private Long firstChoiceCount;
    private Long secondChoiceCount;
    private Boolean isClosed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    private List<TopicImageItem> images;

    @Builder
    public TopicDetailItem(Long topicId, CategoryItem categoryItem, String title, String description, String firstChoice, String secondChoice, AuthorInfoItem author, Long viewCount, Long commentCount, Long favoriteCount, Long voteCount, Long firstChoiceCount, Long secondChoiceCount, Boolean isClosed, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime closedAt, List<TopicImageItem> images) {
        this.topicId = topicId;
        this.categoryItem = categoryItem;
        this.title = title;
        this.description = description;
        this.firstChoice = firstChoice;
        this.secondChoice = secondChoice;
        this.author = author;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.favoriteCount = favoriteCount;
        this.voteCount = voteCount;
        this.firstChoiceCount = firstChoiceCount;
        this.secondChoiceCount = secondChoiceCount;
        this.isClosed = isClosed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.closedAt = closedAt;
        this.images = images;
    }


    public static TopicDetailItem from(Topic topic) {
        Member author = topic.getAuthor();
        Category category = topic.getCategory();

        return TopicDetailItem.builder()
                .topicId(topic.getId())
                .title(topic.getTitle())
                .description(topic.getDescription())
                .firstChoice(topic.getFirstChoice())
                .secondChoice(topic.getSecondChoice())
                .commentCount(0L) // TODO
                .viewCount(topic.getViewCount())
                .favoriteCount(0L) // TODO
                .voteCount(0L) // TODO
                .firstChoiceCount(50L)
                .secondChoiceCount(50L)
                .images(topic.getImages().stream()
                        .map(TopicImageItem::from)
                        .toList())
                .isClosed(topic.getIsClosed())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .closedAt(topic.getClosedAt())
                .author(AuthorInfoItem.from(author))
                .categoryItem(CategoryItem.from(category))
                .build();
    }
}
