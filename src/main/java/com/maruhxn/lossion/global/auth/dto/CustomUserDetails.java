package com.maruhxn.lossion.global.auth.dto;

import com.maruhxn.lossion.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(member.getRole().name());
        authorities.add(simpleGrantedAuthority);
        return authorities;
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
}
