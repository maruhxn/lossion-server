package com.maruhxn.lossion.config;

import com.maruhxn.lossion.util.CustomUserDetailsSecurityContextFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MvcTestConfiguration {

    @Bean
    public CustomUserDetailsSecurityContextFactory userDetailsSecurityContextFactory() {
        return new CustomUserDetailsSecurityContextFactory();
    }
}
