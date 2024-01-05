package com.maruhxn.lossion.domain.member.domain;

import com.maruhxn.lossion.domain.auth.dto.SignUpReq;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
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
    public Member(String accountId, String email, String telNumber, String username, String password) {
        Assert.hasText(email, "아이디는 필수입니다.");
        Assert.hasText(email, "이메일은 필수입니다.");
        Assert.hasText(telNumber, "전화번호는 필수입니다.");
        Assert.hasText(email, "유저명은 필수입니다.");
        Assert.hasText(email, "비밀번호는 필수입니다.");

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

    public static Member of(JwtMemberInfo jwtMemberInfo) {
        Member member = Member.builder()
                .accountId(jwtMemberInfo.getAccountId())
                .email(jwtMemberInfo.getEmail())
                .telNumber(jwtMemberInfo.getTelNumber())
                .username(jwtMemberInfo.getUsername())
                .password("fakepassword")
                .build();
        member.setRole(jwtMemberInfo.getRole());
        member.setProfileImage(jwtMemberInfo.getProfileImage());
        return member;
    }

    public void setRole(String role) {
        this.role = Role.valueOf(role);
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void verifyEmail() {
        this.isVerified = true;
    }
}
