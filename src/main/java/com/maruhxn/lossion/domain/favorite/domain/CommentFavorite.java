package com.maruhxn.lossion.domain.favorite.domain;

import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CommentFavorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private Comment comment;

    @Builder
    public CommentFavorite(Member member, Comment comment) {
        Assert.notNull(member, "유저 정보는 필수입니다.");
        Assert.notNull(comment, "댓글 정보는 필수입니다.");

        this.member = member;
        this.comment = comment;
    }
}
