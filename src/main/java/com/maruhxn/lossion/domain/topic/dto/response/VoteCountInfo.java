package com.maruhxn.lossion.domain.topic.dto.response;

import com.maruhxn.lossion.domain.topic.domain.Vote;
import com.maruhxn.lossion.domain.topic.domain.VoteType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteCountInfo {
    private Long voteCount;
    private Long firstChoiceCount;
    private Long secondChoiceCount;

    @Builder
    public VoteCountInfo(Long voteCount, Long firstChoiceCount, Long secondChoiceCount) {
        this.voteCount = voteCount;
        this.firstChoiceCount = firstChoiceCount;
        this.secondChoiceCount = secondChoiceCount;
    }

    public static VoteCountInfo from(List<Vote> votes) {
        return VoteCountInfo.builder()
                .voteCount((long) votes.size())
                .firstChoiceCount(countVoteType(votes, VoteType.FIRST))
                .secondChoiceCount(countVoteType(votes, VoteType.SECOND))
                .build();
    }

    private static long countVoteType(List<Vote> votes, VoteType voteType) {
        return votes.stream()
                .filter(vote -> vote.getVoteType().equals(voteType))
                .count();
    }
}
