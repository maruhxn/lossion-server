package com.maruhxn.lossion.util;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class CustomUserDetailsSecurityContextFactory implements WithSecurityContextFactory<CustomWithUserDetails> {

    @Override
    public SecurityContext createSecurityContext(CustomWithUserDetails withUserDetails) {
        String accountId = withUserDetails.accountId();
        String password = withUserDetails.password();

        Member findMember = Member.builder()
                .id(1L)
                .accountId(accountId)
                .password(password)
                .username("tester")
                .telNumber("01000000000")
                .email("test@test.com")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(findMember);

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
