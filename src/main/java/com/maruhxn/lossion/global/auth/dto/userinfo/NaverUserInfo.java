package com.maruhxn.lossion.global.auth.dto.userinfo;

import com.maruhxn.lossion.domain.member.domain.OAuthProvider;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.maruhxn.lossion.domain.member.domain.OAuthProvider.NAVER;

@RequiredArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public OAuthProvider getProvider() {
        return NAVER;
    }

    @Override
    public String getSnsId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("nickname");
    }

    @Override
    public String getProfileImage() {
        return (String) attributes.get("profile_image");
    }

    @Override
    public String getAccountId() {
        return "naver_" + getSnsId();
    }

    @Override
    public Boolean getIsVerified() {
        return true;
    }
}
