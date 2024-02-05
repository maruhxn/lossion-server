package com.maruhxn.lossion.global.auth.dto.userinfo;

import com.maruhxn.lossion.domain.member.domain.OAuthProvider;

public interface OAuth2UserInfo {
    OAuthProvider getProvider();

    String getSnsId();

    String getEmail();

    String getUsername();

    String getProfileImage();

    String getAccountId();

    Boolean getIsVerified();

}
