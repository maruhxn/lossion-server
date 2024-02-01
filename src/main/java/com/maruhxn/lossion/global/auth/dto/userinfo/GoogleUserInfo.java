package com.maruhxn.lossion.global.auth.dto.userinfo;

import com.maruhxn.lossion.domain.member.domain.OAuthProvider;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.maruhxn.lossion.domain.member.domain.OAuthProvider.GOOGLE;

@RequiredArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public OAuthProvider getProvider() {
        return GOOGLE;
    }

    @Override
    public String getSnsId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileImage() {
        return (String) attributes.get("picture");
    }

    @Override
    public String getTelNumber() {
        return String.valueOf((Double) Math.floor(10000000000L + Math.random() * 90000000000L));
    }

    @Override
    public String getAccountId() {
        return "google_" + getSnsId();
    }

    @Override
    public Boolean getIsVerified() {
        return (Boolean) attributes.get("email_verified");
    }
}
