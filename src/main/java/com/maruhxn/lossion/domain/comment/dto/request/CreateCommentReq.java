package com.maruhxn.lossion.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCommentReq {

    @NotBlank(message = "내용은 비어있을 수 없습니다.")
    private String text;

    private Long replyToId;

    @Builder
    public CreateCommentReq(String text, Long replyToId) {
        this.text = text;
        this.replyToId = replyToId;
    }
}
