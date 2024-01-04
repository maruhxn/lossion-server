package com.maruhxn.lossion.global.auth.provider;

import com.maruhxn.lossion.global.auth.CustomUserDetails;
import com.maruhxn.lossion.global.auth.service.JwtUserDetailsService;
import com.maruhxn.lossion.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accountId = authentication.getName();
        String password = (String) authentication.getCredentials();

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(accountId);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException(ErrorCode.INCORRECT_PASSWORD.getMessage());
        }

        return UsernamePasswordAuthenticationToken.authenticated(
                userDetails.getMember(),
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
