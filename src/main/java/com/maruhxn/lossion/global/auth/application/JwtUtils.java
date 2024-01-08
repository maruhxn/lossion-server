package com.maruhxn.lossion.global.auth.application;

import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.dto.TokenDto;
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
    public static String USERNAME_CLAIM = "username";
    public static String TEL_NUMBER_CLAIM = "telNumber";
    public static String EMAIL_CLAIM = "email";
    public static String PROFILE_IMAGE_CLAIM = "profileImage";
    public static String IS_VERIFIED_CLAIM = "isVerified";
    public static String ROLE_CLAIM = "role";

    public JwtUtils(@Value("${jwt.secret-key}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    public TokenDto createJwt(JwtMemberInfo jwtMemberInfo) {

        String accessToken = generateAccessToken(jwtMemberInfo);
        String refreshToken = generateRefreshToken(jwtMemberInfo);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateRefreshToken(JwtMemberInfo jwtMemberInfo) {
        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .subject(jwtMemberInfo.getAccountId())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateAccessToken(JwtMemberInfo jwtMemberInfo) {
        return Jwts.builder()
                .subject(jwtMemberInfo.getUsername())
                .claim(ID_CLAIM, jwtMemberInfo.getId())
                .claim(ACCOUNT_ID_CLAIM, jwtMemberInfo.getAccountId())
                .claim(EMAIL_CLAIM, jwtMemberInfo.getEmail())
                .claim(USERNAME_CLAIM, jwtMemberInfo.getUsername())
                .claim(TEL_NUMBER_CLAIM, jwtMemberInfo.getTelNumber())
                .claim(PROFILE_IMAGE_CLAIM, jwtMemberInfo.getProfileImage())
                .claim(IS_VERIFIED_CLAIM, jwtMemberInfo.getIsVerified())
                .claim(ROLE_CLAIM, jwtMemberInfo.getRole())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
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

    public String getEmail(String token) {
        return getPayload(token)
                .get(EMAIL_CLAIM, String.class);
    }

    public String getUsername(String token) {
        return getPayload(token)
                .get(USERNAME_CLAIM, String.class);
    }

    public String getProfileImage(String token) {
        return getPayload(token)
                .get(PROFILE_IMAGE_CLAIM, String.class);
    }

    public String getTelNumber(String token) {
        return getPayload(token)
                .get(TEL_NUMBER_CLAIM, String.class);
    }

    public String getRole(String token) {
        return getPayload(token)
                .get(ROLE_CLAIM, String.class);
    }

    public Boolean getIsVerified(String token) {
        return getPayload(token)
                .get(IS_VERIFIED_CLAIM, Boolean.class);
    }

    public Boolean validate(String token) {
        try {
            return getPayload(token)
                    .getExpiration()
                    .after(new Date());
        } catch (SecurityException e) {
            throw new JwtException("검증 정보가 올바르지 않습니다.");
        } catch (MalformedJwtException e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("기한이 만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 토큰입니다.");
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
