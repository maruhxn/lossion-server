package com.maruhxn.lossion.domain.member.domain;

import com.maruhxn.lossion.domain.auth.dto.SignUpReq;
import com.maruhxn.lossion.global.common.BaseEntity;
import com.maruhxn.lossion.global.common.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static com.maruhxn.lossion.domain.member.domain.OAuthProvider.LOCAL;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String accountId;

    @Column(length = 30, nullable = false, unique = true)
    private String email;

    @Column(length = 10, nullable = false, unique = true)
    private String username;

    private String password;

    @Column(length = 15, nullable = false, unique = true)
    private String telNumber;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'LOCAL'")
    private OAuthProvider provider;

    @Column(unique = true)
    private String snsId;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Boolean isVerified;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ROLE_USER'")
    private Role role;

    @Builder
    public Member(Long id, String accountId, String email, String telNumber, String username, String password, String profileImage, Boolean isVerified, OAuthProvider provider, String snsId) {
        Assert.hasText(accountId, "아이디는 필수입니다.");
        Assert.hasText(email, "이메일은 필수입니다.");
        Assert.hasText(telNumber, "전화번호는 필수입니다.");
        Assert.hasText(username, "유저명은 필수입니다.");

        this.id = id;
        this.accountId = accountId;
        this.email = email;
        this.telNumber = telNumber;
        this.username = username;
        this.password = password;
        this.profileImage = StringUtils.hasText(profileImage) ? profileImage : Constants.BASIC_PROFILE_IMAGE_NAME;
        this.provider = provider != null ? provider : LOCAL;
        this.snsId = StringUtils.hasText(snsId) ? snsId : null;
        this.role = Role.ROLE_USER;
        this.isVerified = isVerified != null ? isVerified : false;
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

    public void rewriteOAuth2User(String username, String accountId, String hashedPwd) {
        this.provider = LOCAL;
        this.username = username;
        this.accountId = accountId;
        this.password = hashedPwd;
    }

    /**
     * 테스트용 메서드
     */
    public void unVerifyEmail() {
        this.isVerified = false;
    }

    public void updateProfile(String username, String email, String newProfileImageName) {
        if (StringUtils.hasText(username)) this.username = username;
        if (StringUtils.hasText(email)) {
            this.email = email;
            this.isVerified = false;
        }
        if (StringUtils.hasText(newProfileImageName)) this.profileImage = newProfileImageName;
    }

    public void updatePassword(String newPassword) {
        Assert.hasText(newPassword, "비밀번호는 필수입니다.");
        this.password = newPassword;
    }
}
