package com.maruhxn.lossion.global.auth.dto;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.auth.application.JwtUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
@ToString
public class JwtMemberInfo {
    private final Long id;
    private final String accountId;
    private final String email;
    private final String username;
    private final String telNumber;
    private final String profileImage;
    private final Boolean isVerified;
    private final String role;

    public static JwtMemberInfo from(CustomUserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        GrantedAuthority authority = authorities.iterator().next();
        String role = authority.getAuthority();

        return JwtMemberInfo.builder()
                .id(userDetails.getId())
                .accountId(userDetails.getAccountId())
                .email(userDetails.getEmail())
                .username(userDetails.getUsername())
                .telNumber(userDetails.getTelNumber())
                .profileImage(userDetails.getProfileImage())
                .isVerified(userDetails.isEnabled())
                .role(role)
                .build();
    }

    public static JwtMemberInfo of(JwtUtils jwtUtils, String token) {
        return JwtMemberInfo.builder()
                .id(jwtUtils.getId(token))
                .accountId(jwtUtils.getAccountId(token))
                .email(jwtUtils.getEmail(token))
                .username(jwtUtils.getUsername(token))
                .telNumber(jwtUtils.getTelNumber(token))
                .profileImage(jwtUtils.getProfileImage(token))
                .isVerified(jwtUtils.getIsVerified(token))
                .role(jwtUtils.getRole(token))
                .build();
    }

    public static JwtMemberInfo from(Member member) {
        return JwtMemberInfo.builder()
                .id(member.getId())
                .accountId(member.getAccountId())
                .email(member.getEmail())
                .username(member.getUsername())
                .telNumber(member.getTelNumber())
                .profileImage(member.getProfileImage())
                .isVerified(member.getIsVerified())
                .role(member.getRole().name())
                .build();
    }

    public List<GrantedAuthority> extractAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(this.getRole());
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

}
