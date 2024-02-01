package com.maruhxn.lossion.global.auth.dto.userinfo;

import com.maruhxn.lossion.domain.member.domain.OAuthProvider;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.maruhxn.lossion.domain.member.domain.OAuthProvider.KAKAO;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public OAuthProvider getProvider() {
        return KAKAO;
    }

    @Override
    public String getSnsId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return (String) ((Map) attributes.get("kakao_account")).get("email");
    }

    @Override
    public String getUsername() {
        return (String) ((Map) attributes.get("properties")).get("nickname");
    }

    @Override
    public String getProfileImage() {
        return (String) ((Map) attributes.get("properties")).get("profile_image");
    }

    @Override
    public String getTelNumber() {
        return String.valueOf((Double) Math.floor(10000000000L + Math.random() * 90000000000L));
    }

    @Override
    public String getAccountId() {
        return "kakao_" + getSnsId();
    }

    @Override
    public Boolean getIsVerified() {
        return (Boolean) ((Map) attributes.get("kakao_account")).get("is_email_verified");
    }
}
