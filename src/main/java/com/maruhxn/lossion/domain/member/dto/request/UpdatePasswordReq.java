package com.maruhxn.lossion.domain.member.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdatePasswordReq {
    @Size(min = 2, max = 20, message = "비밀번호는 2 ~ 20 글자입니다.")
    private String currPassword;

    @Size(min = 2, max = 20, message = "비밀번호는 2 ~ 20 글자입니다.")
    private String newPassword;

    @Size(min = 2, max = 20, message = "비밀번호는 2 ~ 20 글자입니다.")
    private String confirmNewPassword;

    @Builder
    public UpdatePasswordReq(String currPassword, String newPassword, String confirmNewPassword) {
        this.currPassword = currPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}