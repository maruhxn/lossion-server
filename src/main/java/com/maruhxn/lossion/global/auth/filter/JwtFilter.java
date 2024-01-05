package com.maruhxn.lossion.global.auth.filter;

import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.provider.JwtProvider;
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

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 추출
        String token = authorization.split(" ")[1];

        // 토큰에서 정보 추출
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.of(jwtProvider, token);

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
        return request.getServletPath().equals("/api/auth/login");
    }
}
