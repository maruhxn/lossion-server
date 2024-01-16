package com.maruhxn.lossion.domain.comment.domain;

import com.maruhxn.lossion.domain.comment.dto.request.CreateCommentReq;
import com.maruhxn.lossion.domain.favorite.domain.CommentFavorite;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id", referencedColumnName = "id")
    private Comment replyTo;

    @OneToMany(mappedBy = "replyTo", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentFavorite> favorites = new ArrayList<>();

    private String groupId;

    @Builder
    public Comment(String text, Member author, Topic topic, String groupId) {

        Assert.hasText(text, "내용은 필수입니다.");
        Assert.notNull(author, "작성자 정보는 필수입니다.");
        Assert.notNull(topic, "주제 정보는 필수입니다.");

        this.text = text;
        this.author = author;
        this.topic = topic;
        this.groupId = groupId;
    }

    public static Comment of(Member author, Topic topic, CreateCommentReq req, String groupId) {
        return Comment.builder()
                .text(req.getText())
                .topic(topic)
                .author(author)
                .groupId(groupId)
                .build();
    }

    // 연관관계 메서드 //
    public void addReply(Comment reply) {
        replies.add(reply);
        reply.groupId = this.groupId;
        reply.replyTo = this;
    }

    // 편의 메서드 //
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public void updateText(String text) {
        this.text = text;
    }
}