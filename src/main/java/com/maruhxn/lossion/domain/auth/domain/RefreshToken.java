package com.maruhxn.lossion.domain.auth.domain;

import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @NotBlank
    private String refreshToken;

    @NotBlank
    private String accountId;

    @NotBlank
    private String email;

    public RefreshToken(String token, String accountId, String email) {
        this.refreshToken = token;
        this.accountId = accountId;
        this.email = email;
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }
}
