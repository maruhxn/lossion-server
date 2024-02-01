package com.maruhxn.lossion.global.auth.application;

import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.member.domain.OAuthProvider;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import com.maruhxn.lossion.global.auth.dto.userinfo.GoogleUserInfo;
import com.maruhxn.lossion.global.auth.dto.userinfo.KakaoUserInfo;
import com.maruhxn.lossion.global.auth.dto.userinfo.NaverUserInfo;
import com.maruhxn.lossion.global.auth.dto.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static com.maruhxn.lossion.domain.member.domain.OAuthProvider.valueOf;

@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = switch (valueOf(provider.toUpperCase())) {
            case GOOGLE -> new GoogleUserInfo(oAuth2User.getAttributes());
            case KAKAO -> new KakaoUserInfo((Map<String, Object>) oAuth2User.getAttributes());
            case NAVER -> new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
            default -> throw new OAuth2AuthenticationException("일치하는 제공자가 없습니다.");
        };

        Member member = createOrGetOAuth2Member(oAuth2UserInfo, oAuth2UserInfo.getProvider());

        return new CustomUserDetails(member, oAuth2User.getAttributes());
    }

    private Member createOrGetOAuth2Member(OAuth2UserInfo userInfo, OAuthProvider provider) {
        Member member;
        Optional<Member> optionalMember = memberRepository.findByEmail(userInfo.getEmail());

        if (optionalMember.isEmpty()) { // 이메일 중복 체크
            Long memberCntByUsername = memberRepository.countByUsername(userInfo.getUsername()); // 해당 유저명과 일치하는 유저 수 체크

            member = Member.builder()
                    .accountId(userInfo.getAccountId())
                    .username(memberCntByUsername.equals(0L) ? userInfo.getUsername() : userInfo.getUsername() + memberCntByUsername)
                    .provider(provider)
                    .snsId(userInfo.getSnsId())
                    .email(userInfo.getEmail())
                    .telNumber(userInfo.getTelNumber())
                    .profileImage(userInfo.getProfileImage())
                    .isVerified(userInfo.getIsVerified())
                    .build();

            memberRepository.save(member);
        } else {
            member = optionalMember.get();
        }
        return member;
    }
}

/* google
  {
       sub=103058387739722400130,
       name=안창범,
       given_name=창범,
       family_name=안,
       picture=https://lh3.googleusercontent.com/a/AEdFTp5SiCyTaOLog9sDPN6QhWwsUj7xPbfj4HQF0fdC=s96-c,
       email=chb20050@gmail.com,
       email_verified=true,
       locale=ko
  }
 */

/* kakao
  {
   id=3323278411,
   connected_at=2024-01-31T13:09:57Z,
   properties={
       nickname=maruhxn,
       profile_image=http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg,
       thumbnail_image=http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_110x110.jpg
   },
   kakao_account={
       profile_nickname_needs_agreement=false,
       profile_image_needs_agreement=false,
       profile={
           nickname=maruhxn,
           thumbnail_image_url=http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_110x110.jpg,
           profile_image_url=http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg,
           is_default_image=true
       },
       has_email=true,
       email_needs_agreement=false,
       is_email_valid=true,
       is_email_verified=true,
       email=maruhan1016@gmail.com
   }
  }
 */

/* naver
{
    resultcode=00,
    message=success,
    response={
        id=TKx2Nq5TZUuvHrm_u_CECK8lrRr5RlUUlwWG84G_bKw,
        nickname=고지완,
        profile_image=https://phinf.pstatic.net/contact/20221016_99/1665899169255F1Dx2_JPEG/image.jpg,
        email=lhk6397@naver.com,
        mobile=010-2368-6397,
        mobile_e164=+821023686397,
        name=고지완
    }
}
 */