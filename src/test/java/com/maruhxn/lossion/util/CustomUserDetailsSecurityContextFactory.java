package com.maruhxn.lossion.util;

import com.maruhxn.lossion.global.auth.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class CustomUserDetailsSecurityContextFactory implements WithSecurityContextFactory<CustomWithUserDetails> {

    private final JwtAuthenticationProvider authenticationProvider;

    @Override
    public SecurityContext createSecurityContext(CustomWithUserDetails withUserDetails) {
        String accountId = withUserDetails.accountId();
        String password = withUserDetails.password();

        UsernamePasswordAuthenticationToken unauthenticated =
                UsernamePasswordAuthenticationToken.unauthenticated(accountId, password);

        Authentication authentication = authenticationProvider.authenticate(unauthenticated);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        return context;

    }
}
