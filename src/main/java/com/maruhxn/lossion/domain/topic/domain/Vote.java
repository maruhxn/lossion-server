package com.maruhxn.lossion.domain.topic.domain;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dto.request.VoteRequest;
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
public class Vote extends BaseEntity {

    private VoteType voteType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", referencedColumnName = "id")
    private Member voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @Builder
    public Vote(VoteType voteType, Member voter, Topic topic) {
        Assert.notNull(voter, "투표자 정보는 필수입니다.");
        Assert.notNull(topic, "주제 정보는 필수입니다.");

        this.voteType = voteType;
        this.voter = voter;
        this.topic = topic;
    }

    public static Vote of(Member member, Topic topic, VoteRequest req) {
        return Vote.builder()
                .voteType(req.getVoteType())
                .voter(member)
                .topic(topic)
                .build();
    }

    public void updateVoteType(VoteRequest req) {
        if (this.getVoteType().equals(req.getVoteType())) {
            this.voteType = null;
        } else {
            this.voteType = req.getVoteType();
        }
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
