package com.maruhxn.lossion.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.global.auth.application.JwtService;
import com.maruhxn.lossion.global.auth.application.JwtUserDetailsService;
import com.maruhxn.lossion.global.auth.application.JwtUtils;
import com.maruhxn.lossion.global.auth.application.OAuth2UserService;
import com.maruhxn.lossion.global.auth.filter.JwtAuthenticationFilter;
import com.maruhxn.lossion.global.auth.filter.JwtAuthorizationFilter;
import com.maruhxn.lossion.global.auth.filter.JwtExceptionFilter;
import com.maruhxn.lossion.global.auth.handler.JwtAccessDeniedHandler;
import com.maruhxn.lossion.global.auth.handler.JwtAuthenticationEntryPoint;
import com.maruhxn.lossion.global.auth.handler.OAuth2SuccessHandler;
import com.maruhxn.lossion.global.auth.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtils jwtUtils;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(corsCustomizer ->
                        corsCustomizer
                                .configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authz ->
                        authz
                                .requestMatchers(
                                        "/",
                                        "/test/**",
                                        "/login/oauth2/**",
                                        "/api/auth/sign-up",
                                        "/api/auth/refresh",
                                        "/api/auth/anonymous/send-verify-email",
                                        "/api/auth/anonymous/get-token",
                                        "/api/auth/anonymous/password",
                                        "/api/categories"
                                ).permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/topics", "/api/topics/{topicId}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/topics/{topicId}/comments", "/api/topics/{topicId}/comments/groups/{groupId}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/files", "/api/files/**").permitAll()
                                .requestMatchers("/api/auth/test").authenticated()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 ->
                        oauth2
                                .authorizationEndpoint(endPoint -> endPoint.baseUri("/api/auth/oauth2"))
                                .redirectionEndpoint(endPoint -> endPoint.baseUri("/login/oauth2/code/*"))
                                .userInfoEndpoint(endPoint -> endPoint.userService(oAuth2UserService()))
                                .successHandler(oAuth2SuccessHandler())
                )
                .addFilterAt(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter(), JwtAuthorizationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint())
                                .accessDeniedHandler(jwtAccessDeniedHandler())
                );

        return http.build();
    }

    @Bean
    public OAuth2UserService oAuth2UserService() {
        return new OAuth2UserService(memberRepository);
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(jwtUtils, jwtService);
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils, objectMapper, jwtService);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager());
        return jwtAuthenticationFilter;
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtUserDetailsService, passwordEncoder());
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtils, jwtUserDetailsService);
    }

    @Bean
    public JwtExceptionFilter jwtExceptionFilter() {
        return new JwtExceptionFilter(objectMapper);
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) ->
                web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
