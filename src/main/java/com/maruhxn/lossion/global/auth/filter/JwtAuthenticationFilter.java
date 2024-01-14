package com.maruhxn.lossion.global.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maruhxn.lossion.domain.auth.dto.LoginReq;
import com.maruhxn.lossion.global.auth.application.JwtService;
import com.maruhxn.lossion.global.auth.application.JwtUtils;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.dto.TokenDto;
import com.maruhxn.lossion.global.common.dto.BaseResponse;
import com.maruhxn.lossion.global.common.dto.DataResponse;
import com.maruhxn.lossion.global.common.dto.ErrorResponse;
import com.maruhxn.lossion.global.error.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Set;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;


    public JwtAuthenticationFilter(JwtUtils jwtUtils, ObjectMapper objectMapper, JwtService jwtService) {
        super(new AntPathRequestMatcher("/api/auth/login"));
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
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
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(userDetails);
        TokenDto tokenDto = jwtUtils.createJwt(jwtMemberInfo);

        jwtService.saveRefreshToken(jwtMemberInfo, tokenDto);
        jwtUtils.setHeader(response, tokenDto);

        DataResponse<TokenDto> responseDto = DataResponse.of("로그인 성공", tokenDto);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), responseDto);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        BaseResponse responseDto = null;

        if (exception instanceof BadCredentialsException) {
            responseDto = ErrorResponse.of(ErrorCode.INCORRECT_PASSWORD);
        } else if (exception instanceof UsernameNotFoundException) {
            responseDto = ErrorResponse.of(ErrorCode.NOT_FOUND_MEMBER);
        }

        objectMapper.writeValue(response.getWriter(), responseDto);
    }

}
