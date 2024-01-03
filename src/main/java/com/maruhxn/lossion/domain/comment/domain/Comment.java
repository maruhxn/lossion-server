package com.maruhxn.lossion.domain.comment.domain;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"author", "topic", "parent", "replies"})
public class Comment extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();

    @Builder
    public Comment(String content, Member author, Topic topic) {

        Assert.hasText(content, "내용은 필수입니다.");
        Assert.notNull(author, "작성자 정보는 필수입니다.");
        Assert.notNull(topic, "주제 정보는 필수입니다.");

        this.content = content;
        this.author = author;
        this.topic = topic;
    }

    // 연관관계 메서드 //
    public void addReply(Comment reply) {
        replies.add(reply);
        reply.parent = this;
    }

    // 편의 메서드 //
    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}