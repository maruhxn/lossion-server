package com.maruhxn.lossion.domain.favorite.domain;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.util.Assert;

/**
 * 있거나, 없거나의 2가지 경우의 수이므로 기본 필드 외에 어떠한 다른 필드도 필요하지 않음.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class TopicFavorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @Builder
    public TopicFavorite(Member member, Topic topic) {
        Assert.notNull(member, "유저 정보는 필수입니다.");
        Assert.notNull(topic, "주제 정보는 필수입니다.");

        this.member = member;
        this.topic = topic;
    }
}
