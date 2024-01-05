package com.maruhxn.lossion.global.auth.dto;

import com.maruhxn.lossion.global.auth.provider.JwtProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class JwtMemberInfo {
    private final String accountId;
    private final String email;
    private final String username;
    private final String telNumber;
    private final String profileImage;
    private final String role;

    public static JwtMemberInfo from(CustomUserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        GrantedAuthority authority = authorities.iterator().next();
        String role = authority.getAuthority();

        return JwtMemberInfo.builder()
                .accountId(userDetails.getAccountId())
                .email(userDetails.getEmail())
                .username(userDetails.getUsername())
                .telNumber(userDetails.getTelNumber())
                .profileImage(userDetails.getProfileImage())
                .role(role)
                .build();
    }

    public static JwtMemberInfo of(JwtProvider jwtProvider, String token) {
        return JwtMemberInfo.builder()
                .accountId(jwtProvider.getAccountId(token))
                .email(jwtProvider.getEmail(token))
                .username(jwtProvider.getUsername(token))
                .telNumber(jwtProvider.getTelNumber(token))
                .profileImage(jwtProvider.getProfileImage(token))
                .role(jwtProvider.getRole(token))
                .build();
    }

    public List<GrantedAuthority> extractAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(this.getRole());
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

}
