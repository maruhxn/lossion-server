package com.maruhxn.lossion.global.auth.dto;

import com.maruhxn.lossion.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final Member member;
    private Map<String, Object> attributes;

    public CustomUserDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(member.getRole().name());
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

    public Member getMember() {
        return member;
    }

    public Long getId() {
        return member.getId();
    }

    public String getAccountId() {
        return member.getAccountId();
    }

    public String getEmail() {
        return member.getEmail();
    }

    public String getTelNumber() {
        return member.getTelNumber();
    }

    public String getProfileImage() {
        return member.getProfileImage();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return member.getIsVerified();
    }

    @Override
    public String getName() {
        return null;
    }
}
