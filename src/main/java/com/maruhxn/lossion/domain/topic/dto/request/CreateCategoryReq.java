package com.maruhxn.lossion.domain.topic.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCategoryReq {
    @NotEmpty(message = "카테고리명은 비어있을 수 없습니다.")
    @Size(max = 30, message = "카테고리명은 최대 30 글자입니다.")
    private String name;

    @Builder
    public CreateCategoryReq(String name) {
        this.name = name;
    }
}
