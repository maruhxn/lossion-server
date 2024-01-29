package com.maruhxn.lossion.global.auth.application;

import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.dto.TokenDto;
import com.maruhxn.lossion.global.error.ErrorCode;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.maruhxn.lossion.global.common.Constants.*;

@Component
public class JwtUtils {

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;
    private SecretKey secretKey;
    private JwtParser jwtParser;
    public static String ID_CLAIM = "id";
    public static String ACCOUNT_ID_CLAIM = "accountId";

    public JwtUtils(@Value("${jwt.secret-key}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    public TokenDto createJwt(JwtMemberInfo jwtMemberInfo) {

        String accessToken = generateAccessToken(jwtMemberInfo, new Date());
        String refreshToken = generateRefreshToken(jwtMemberInfo, new Date());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateRefreshToken(JwtMemberInfo jwtMemberInfo, Date now) {
        return Jwts.builder()
                .subject(String.valueOf(jwtMemberInfo.getAccountId()))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateAccessToken(JwtMemberInfo jwtMemberInfo, Date now) {
        return Jwts.builder()
                .subject(String.valueOf(jwtMemberInfo.getId()))
                .claim(ACCOUNT_ID_CLAIM, jwtMemberInfo.getAccountId())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtExpiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims getPayload(String token) {
        return jwtParser
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getId(String token) {
        return getPayload(token).get(ID_CLAIM, Long.class);
    }

    public String getAccountId(String token) {
        return getPayload(token).get(ACCOUNT_ID_CLAIM, String.class);
    }

    public boolean validate(String token) {
        try {
            return getPayload(token)
                    .getExpiration()
                    .after(new Date());
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException e) {
            throw new JwtException(ErrorCode.INVALID_TOKEN.getMessage());
        }
    }


    public String getBearerTokenToString(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.split(" ")[1];
        }
        return null;
    }

    public void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken());
        response.addHeader(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken());
    }

}
