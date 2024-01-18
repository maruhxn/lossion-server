package com.maruhxn.lossion.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetTokenReq {

    @Size(min = 5, max = 10, message = "아이디는 5 ~ 30 글자입니다.")
    private String accountId;

    @Email(message = "이메일 형식에 맞추어 입력해주세요.")
    @NotEmpty(message = "이메일을 입력해주세요.")
    private String email;

    @NotEmpty(message = "토큰을 입력해주세요.")
    private String payload;

    @Builder
    public GetTokenReq(String accountId, String email, String payload) {
        this.accountId = accountId;
        this.email = email;
        this.payload = payload;
    }
}
