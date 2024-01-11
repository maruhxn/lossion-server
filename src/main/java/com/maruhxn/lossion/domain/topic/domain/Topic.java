package com.maruhxn.lossion.domain.topic.domain;

import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.favorite.domain.TopicFavorite;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dto.request.CreateTopicReq;
import com.maruhxn.lossion.domain.topic.dto.request.UpdateTopicReq;
import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"author", "comments"})
@DynamicInsert
public class Topic extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long viewCount;

    @Column(nullable = false)
    private String firstChoice;

    @Column(nullable = false)
    private String secondChoice;

    @Column(nullable = false)
    private LocalDateTime closedAt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Boolean isClosed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<TopicImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<TopicFavorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    @Builder
    public Topic(String title, String description, String firstChoice, String secondChoice, LocalDateTime closedAt, LocalDateTime now, Member author, Category category) {
        Assert.hasText(title, "제목은 필수입니다.");
        Assert.hasText(description, "내용은 필수입니다.");
        Assert.hasText(firstChoice, "1번 선택지는 필수입니다.");
        Assert.hasText(secondChoice, "2번 선택지는 필수입니다.");
        Assert.notNull(closedAt, "토론 종료 시각은 필수입니다.");
        Assert.notNull(author, "유저 정보는 필수입니다.");
        Assert.notNull(category, "카테고리 정보는 필수입니다.");

        if (closedAt.isEqual(now) || closedAt.isBefore(now)) {
            throw new IllegalArgumentException("토론 종료 시각은 현재 시각 이후로 설정해야 합니다.");
        }

        this.title = title;
        this.description = description;
        this.firstChoice = firstChoice;
        this.secondChoice = secondChoice;
        this.closedAt = closedAt;
        this.isClosed = false;
        this.author = author;
        this.category = category;
        this.viewCount = 0L;
    }

    public static Topic of(Member author, Category category, List<TopicImage> images, CreateTopicReq req) {
        Topic topic = Topic.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .closedAt(req.getClosedAt())
                .now(LocalDateTime.now())
                .firstChoice(req.getFirstChoice())
                .secondChoice(req.getSecondChoice())
                .author(author)
                .category(category)
                .build();

        if (!images.isEmpty()) {
            images.forEach(topic::addTopicImage);
        }

        return topic;
    }

    // 연관관계 메서드 //

    public void addTopicImage(TopicImage topicImage) {
        topicImage.setTopic(this);
        images.add(topicImage);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comments.addAll(comment.getReplies());
        comment.setTopic(this);
    }

    public void addVote(Vote vote) {
        vote.setTopic(this);
        votes.add(vote);
    }

    public void addViewCount() {
        viewCount += 1;
    }

    public void updateCloseStatus(LocalDateTime now) {
        this.isClosed = now.isAfter(this.closedAt);
    }

    public void updateTopic(UpdateTopicReq req, List<TopicImage> topicImages) {
        if (StringUtils.hasText(req.getTitle())) {
            this.title = req.getTitle();
        }

        if (StringUtils.hasText(req.getDescription())) {
            this.description = req.getDescription();
        }

        if (StringUtils.hasText(req.getFirstChoice())) {
            this.firstChoice = req.getFirstChoice();
        }

        if (StringUtils.hasText(req.getSecondChoice())) {
            this.secondChoice = req.getSecondChoice();
        }

        if (req.getClosedAt() != null) {
            this.closedAt = req.getClosedAt();
        }

        if (!topicImages.isEmpty()) {
            for (TopicImage image : topicImages) {
                addTopicImage(image);
            }
        }
    }

    public void changeCategory(Category category) {
        this.category = category;
    }
}
