package com.maruhxn.lossion.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpReq {

    @NotEmpty(message = "아이디를 입력해주세요.")
    @Size(min = 5, max = 10, message = "아이디는 5 ~ 30 글자입니다.")
    private String accountId;

    @Email(message = "이메일 형식에 맞추어 입력해주세요.")
    @NotEmpty(message = "이메일을 입력해주세요.")
    private String email;

    @NotEmpty(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^[0-9]{11,13}$")
    private String telNumber;

    @NotEmpty(message = "유저명을 입력해주세요.")
    @Size(min = 2, max = 10, message = "유저명은 2 ~ 15 글자입니다.")
    private String username;

    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Size(min = 2, max = 20, message = "비밀번호는 2 ~ 20 글자입니다.")
    private String password;

    @NotEmpty(message = "비밀번호 확인값을 입력해주세요.")
    @Size(min = 2, max = 20, message = "비밀번호 확인값은 2 ~ 20 글자입니다.")
    private String confirmPassword;

    @Builder
    public SignUpReq(String accountId, String email, String telNumber, String username, String password, String confirmPassword) {
        this.accountId = accountId;
        this.email = email;
        this.telNumber = telNumber;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    // 편의 메서드
    public void changeRawPwdToHashedPwd(String hashedPwd) {
        this.password = hashedPwd;
    }
}
