package com.maruhxn.lossion.domain.member.domain;

import com.maruhxn.lossion.domain.auth.dto.SignUpReq;
import com.maruhxn.lossion.global.common.BaseEntity;
import com.maruhxn.lossion.global.common.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Member extends BaseEntity {

    @Column(length = 10, nullable = false, unique = true)
    private String accountId;

    @Column(length = 30, nullable = false, unique = true)
    private String email;

    @Column(length = 10, nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 15, nullable = false)
    private String telNumber;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Boolean isVerified;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ROLE_USER'")
    private Role role;

    @Builder
    public Member(Long id, String accountId, String email, String telNumber, String username, String password) {
        Assert.hasText(accountId, "아이디는 필수입니다.");
        Assert.hasText(email, "이메일은 필수입니다.");
        Assert.hasText(telNumber, "전화번호는 필수입니다.");
        Assert.hasText(username, "유저명은 필수입니다.");
        Assert.hasText(password, "비밀번호는 필수입니다.");

        this.id = id;
        this.accountId = accountId;
        this.email = email;
        this.telNumber = telNumber;
        this.username = username;
        this.password = password;
        this.profileImage = Constants.BASIC_PROFILE_IMAGE_NAME;
        this.role = Role.ROLE_USER;
        this.isVerified = false;
    }

    public static Member from(SignUpReq req) {
        return Member.builder()
                .accountId(req.getAccountId())
                .email(req.getEmail())
                .telNumber(req.getTelNumber())
                .username(req.getUsername())
                .password(req.getPassword())
                .build();
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void verifyEmail() {
        this.isVerified = true;
    }

    public void updateProfile(String username, String email, String newProfileImageName) {
        if (StringUtils.hasText(username)) this.username = username;
        if (StringUtils.hasText(email)) {
            this.email = email;
            this.isVerified = false;
        }
        if (StringUtils.hasText(profileImage)) this.profileImage = newProfileImageName;
    }

    public void updatePassword(String newPassword) {
        Assert.hasText(newPassword, "비밀번호는 필수입니다.");
        this.password = newPassword;
    }
}
