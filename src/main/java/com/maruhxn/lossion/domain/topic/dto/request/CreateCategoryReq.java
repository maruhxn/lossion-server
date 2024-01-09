package com.maruhxn.lossion.domain.topic.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCategoryReq {
    @NotEmpty(message = "카테고리명은 비어있을 수 없습니다.")
    private String name;
}
