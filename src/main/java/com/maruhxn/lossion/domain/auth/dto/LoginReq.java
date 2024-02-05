package com.maruhxn.lossion.domain.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginReq {

    @NotEmpty(message = "아이디를 입력해주세요.")
    @Size(min = 5, max = 10, message = "아이디는 5 ~ 30 글자입니다.")
    private String accountId;

    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Size(min = 2, max = 20, message = "비밀번호는 2 ~ 20 글자입니다.")
    private String password;

    @Builder
    public LoginReq(String accountId, String password) {
        this.accountId = accountId;
        this.password = password;
    }
}
