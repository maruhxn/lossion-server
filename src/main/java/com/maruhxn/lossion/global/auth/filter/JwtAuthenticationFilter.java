package com.maruhxn.lossion.global.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maruhxn.lossion.domain.auth.dto.LoginReq;
import com.maruhxn.lossion.global.auth.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Set;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher("/api/auth/login"));
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        LoginReq loginReq = objectMapper.readValue(request.getReader(), LoginReq.class);
        Set<ConstraintViolation<LoginReq>> validate = validator.validate(loginReq);

        if (!validate.isEmpty()) {
            throw new AuthenticationServiceException("유효하지 않은 로그인입니다.");
        }


        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                loginReq.getAccountId(),
                loginReq.getPassword()
        );


        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("성공");
        System.out.println("authResult = " + authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("실패");
    }

//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            String token = parseBearerToken(request);
//
//            if (token == null) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            String email = jwtProvider.validate(token);
//
//            if (email == null) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, AuthorityUtils.NO_AUTHORITIES);
//            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//            securityContext.setAuthentication(authenticationToken);
//
//            SecurityContextHolder.setContext(securityContext);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    // 헤더에서 Bearer Token을 파싱해옴.
//    private String parseBearerToken(HttpServletRequest request) {
//        String authorization = request.getHeader("Authorization");
//
//        boolean hasAuthorization = StringUtils.hasText(authorization);
//        if (!hasAuthorization) return null;
//
//        boolean isBearer = authorization.startsWith("Bearer ");
//        if (!isBearer) return null;
//
//        return authorization.substring(7);
//    }
}
