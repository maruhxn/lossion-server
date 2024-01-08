package com.maruhxn.lossion.config;

import com.maruhxn.lossion.global.auth.provider.JwtAuthenticationProvider;
import com.maruhxn.lossion.util.CustomUserDetailsSecurityContextFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MvcTestConfiguration {

    @Bean
    public CustomUserDetailsSecurityContextFactory userDetailsSecurityContextFactory(
            JwtAuthenticationProvider jwtAuthenticationProvider
    ) {
        return new CustomUserDetailsSecurityContextFactory(jwtAuthenticationProvider);
    }
}
