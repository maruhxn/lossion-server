package com.maruhxn.lossion.global.auth.dto;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.auth.application.JwtUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@RequiredArgsConstructor
@ToString
public class JwtMemberInfo {
    private final Long id;
    private final String accountId;

    public static JwtMemberInfo from(CustomUserDetails userDetails) {
        return JwtMemberInfo.builder()
                .id(userDetails.getId())
                .accountId(userDetails.getAccountId())
                .build();
    }

    public static JwtMemberInfo of(JwtUtils jwtUtils, String token) {
        return JwtMemberInfo.builder()
                .id(jwtUtils.getId(token))
                .accountId(jwtUtils.getAccountId(token))
                .build();
    }

    public static JwtMemberInfo from(Member member) {
        return JwtMemberInfo.builder()
                .id(member.getId())
                .accountId(member.getAccountId())
                .build();
    }

}
