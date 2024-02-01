package com.maruhxn.lossion.global.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maruhxn.lossion.global.auth.application.JwtService;
import com.maruhxn.lossion.global.auth.application.JwtUtils;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.dto.TokenDto;
import com.maruhxn.lossion.global.common.dto.DataResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        CustomUserDetails oAuth2User = (CustomUserDetails) authentication.getPrincipal();

        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(oAuth2User);
        TokenDto tokenDto = jwtUtils.createJwt(jwtMemberInfo);

        jwtService.saveRefreshToken(jwtMemberInfo, tokenDto);
        jwtUtils.setHeader(response, tokenDto);

        DataResponse<TokenDto> responseDto = DataResponse.of("소셜 로그인 성공", tokenDto);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), responseDto);
    }
}

