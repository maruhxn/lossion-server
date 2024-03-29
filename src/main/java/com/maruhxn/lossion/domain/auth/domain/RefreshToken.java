package com.maruhxn.lossion.domain.auth.domain;

import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Builder
    public RefreshToken(String refreshToken, String accountId) {
        this.refreshToken = refreshToken;
        this.accountId = accountId;
    }

    public RefreshToken updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
