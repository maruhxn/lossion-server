package com.maruhxn.lossion.global.auth.handler;

import com.maruhxn.lossion.global.auth.application.JwtService;
import com.maruhxn.lossion.global.auth.application.JwtUtils;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.dto.TokenDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${client.url}")
    private String CLIENT_URL;

    private final JwtUtils jwtUtils;
    private final JwtService jwtService;

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

        response.sendRedirect(CLIENT_URL + "?accessToken=" + tokenDto.getAccessToken() + "&refreshToken=" + tokenDto.getRefreshToken());
    }
}
