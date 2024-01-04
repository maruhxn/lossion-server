package com.maruhxn.lossion.domain.member.domain;

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

    @Column(length = 30, nullable = false, unique = true)
    private String accountId;

    @Column(length = 30, nullable = false, unique = true)
    private String email;

    @Column(length = 15, nullable = false, unique = true)
    private String username;

    @Column(length = 20, nullable = false)
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
}
