package com.maruhxn.lossion.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class UpdateMemberProfileReq {
    @Size(min = 2, max = 10, message = "유저명은 2 ~ 10 글자입니다.")
    private String username;

    @Email(message = "이메일 형식에 맞추어 입력해주세요.")
    private String email;

    private MultipartFile profileImage;

    @Builder
    public UpdateMemberProfileReq(String username, String email, MultipartFile profileImage) {
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileImage(MultipartFile profileImage) {
        this.profileImage = profileImage;
    }
}