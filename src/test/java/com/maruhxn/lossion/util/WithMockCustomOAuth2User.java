package com.maruhxn.lossion.util;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomOAuth2UserSecurityContextFactory.class)
public @interface WithMockCustomOAuth2User {

    String username() default "oauth";

    String email() default "oauth@test.com";

    String picture() default "https://test_profile_image.com";

    String role() default "ROLE_USER";

    String registrationId();
}
