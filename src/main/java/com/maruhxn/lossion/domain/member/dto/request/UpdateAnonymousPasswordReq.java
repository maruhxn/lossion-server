package com.maruhxn.lossion.domain.member.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateAnonymousPasswordReq {
    @Size(min = 2, max = 20, message = "비밀번호는 2 ~ 20 글자입니다.")
    private String newPassword;

    @Size(min = 2, max = 20, message = "비밀번호는 2 ~ 20 글자입니다.")
    private String confirmNewPassword;
}
