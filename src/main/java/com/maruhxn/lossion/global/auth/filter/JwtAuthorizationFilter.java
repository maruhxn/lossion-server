package com.maruhxn.lossion.global.auth.filter;

import com.maruhxn.lossion.global.auth.application.JwtUserDetailsService;
import com.maruhxn.lossion.global.auth.application.JwtUtils;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.maruhxn.lossion.global.common.Constants.ACCESS_TOKEN_HEADER;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final List<String> EXCLUDE_URL =
            List.of(
                    "/",
                    "/test/**",
                    "/api/auth/sign-up",
                    "/api/auth/refresh",
                    "/api/auth/anonymous/send-verify-email",
                    "/api/auth/anonymous/get-token",
                    "/api/auth/anonymous/password",
                    "/api/categories"
            );

    private final JwtUtils jwtUtils;
    private final JwtUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader(ACCESS_TOKEN_HEADER);

        // 토큰 추출
        String token = jwtUtils.getBearerTokenToString(authorization);

        if (!StringUtils.hasText(token) || !jwtUtils.validate(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 정보 추출
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.of(jwtUtils, token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtMemberInfo.getAccountId());

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }
}
