package com.maruhxn.lossion.domain.topic.dto.request;

import com.maruhxn.lossion.domain.topic.domain.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VoteRequest {

    @NotNull(message = "투표 정보는 비어있을 수 없습니다.")
    private VoteType voteType;

    @NotNull(message = "투표 시각은 비어있을 수 없습니다.")
    private LocalDateTime voteAt;

    @Builder
    public VoteRequest(VoteType voteType, LocalDateTime voteAt) {
        this.voteType = voteType;
        this.voteAt = voteAt;
    }


}
