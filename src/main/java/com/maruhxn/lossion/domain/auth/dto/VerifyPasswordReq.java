package com.maruhxn.lossion.domain.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerifyPasswordReq {

    @NotEmpty(message = "현재 비밀번호를 입력해주세요")
    private String currPassword;

    public VerifyPasswordReq(String currPassword) {
        this.currPassword = currPassword;
    }
}
