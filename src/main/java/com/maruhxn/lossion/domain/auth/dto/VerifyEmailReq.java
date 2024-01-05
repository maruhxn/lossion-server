package com.maruhxn.lossion.domain.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerifyEmailReq {

    @NotEmpty(message = "토큰을 입력해주세요.")
    private String payload;
}
