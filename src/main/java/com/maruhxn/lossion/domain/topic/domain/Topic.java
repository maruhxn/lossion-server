package com.maruhxn.lossion.domain.topic.domain;

import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.util.Assert;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Topic(String title, String description, Member author, Category category) {
        Assert.hasText(title, "제목은 필수입니다.");
        Assert.hasText(description, "내용은 필수입니다.");
        Assert.notNull(author, "유저 정보는 필수입니다.");
        Assert.notNull(category, "카테고리 정보는 필수입니다.");

        this.title = title;
        this.description = description;
        this.author = author;
        this.category = category;
        this.viewCount = 0L;
    }

    // 연관관계 메서드 //
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.getReplies().stream().forEach(
                reply -> comments.add(reply)
        );
        comment.setTopic(this);
    }
}
