package com.maruhxn.lossion.util;

import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.member.domain.OAuthProvider;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import com.maruhxn.lossion.global.auth.dto.userinfo.GoogleUserInfo;
import com.maruhxn.lossion.global.auth.dto.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class WithMockCustomOAuth2UserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomOAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomOAuth2User oauth2User) {

        String provider = oauth2User.registrationId();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "111111111111111111111");
        attributes.put("name", oauth2User.username());
        attributes.put("email", oauth2User.email());
        attributes.put("picture", oauth2User.picture());
        attributes.put("email_verified", true);

        OAuth2UserInfo userInfo = new GoogleUserInfo(attributes);

        Member member = Member.builder()
                .accountId(userInfo.getAccountId())
                .username(userInfo.getUsername())
                .provider(OAuthProvider.valueOf(provider.toUpperCase()))
                .snsId(userInfo.getSnsId())
                .email(userInfo.getEmail())
                .telNumber(userInfo.getTelNumber())
                .profileImage(userInfo.getProfileImage())
                .isVerified(userInfo.getIsVerified())
                .build();

        memberRepository.save(member);

        CustomUserDetails userDetails = new CustomUserDetails(member, attributes);

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        return context;
    }
}
