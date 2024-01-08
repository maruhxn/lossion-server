package com.maruhxn.lossion.global.auth.filter;

import com.maruhxn.lossion.global.auth.application.JwtUtils;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.maruhxn.lossion.global.common.Constants.ACCESS_TOKEN_HEADER;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final List<String> EXCLUDE_URL =
            List.of("/",
                    "/api/auth/sign-up",
                    "/api/auth/login",
                    "/api/auth/refresh",
                    "/api/auth/send-anonymous-verify-email",
                    "/api/auth/get-token",
                    "/api/auth/update-anonymous-password");

    private final JwtUtils jwtUtils;

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

        // authority 추출
        List<GrantedAuthority> authorities = jwtMemberInfo.extractAuthorities();

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(
                jwtMemberInfo,
                null,
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean result = EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));

        return result;
    }
}
