package com.maruhxn.lossion.domain.comment.dto.response;

import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dto.response.AuthorInfoItem;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class CommentItem {
    private Long id;
    private String text;
    private AuthorInfoItem author;
    private CommentItem replyTo;
    private String groupId;
    private Long favoriteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    @QueryProjection
    public CommentItem(Long id, String text, Member author, String groupId, Long favoriteCount, Comment replyTo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.text = text;
        this.author = AuthorInfoItem.from(author);
        this.replyTo = replyTo == null ? null : CommentItem.from(replyTo);
        this.groupId = groupId;
        this.favoriteCount = favoriteCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentItem from(Comment reply) {
        return CommentItem.builder()
                .id(reply.getId())
                .text(reply.getText())
                .author(reply.getAuthor())
                .favoriteCount((long) reply.getFavorites().size())
                .replyTo(reply.getReplyTo())
                .groupId(reply.getGroupId())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .build();
    }
}
