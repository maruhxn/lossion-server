package com.maruhxn.lossion.global.auth.provider;

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

@Component
public class JwtProvider {

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;
    private SecretKey secretKey;
    private JwtParser jwtParser;

    public JwtProvider(@Value("${jwt.secret-key}") String secret) {
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
                .claim("accountId", jwtMemberInfo.getAccountId())
                .claim("email", jwtMemberInfo.getEmail())
                .claim("username", jwtMemberInfo.getUsername())
                .claim("telNumber", jwtMemberInfo.getTelNumber())
                .claim("profileImage", jwtMemberInfo.getProfileImage())
                .claim("isVerified", jwtMemberInfo.getIsVerified())
                .claim("role", jwtMemberInfo.getRole())
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

    public String getAccountId(String token) {
        return getPayload(token).get("accountId", String.class);
    }

    public String getEmail(String token) {
        return getPayload(token)
                .get("email", String.class);
    }

    public String getUsername(String token) {
        return getPayload(token)
                .get("username", String.class);
    }

    public String getProfileImage(String token) {
        return getPayload(token)
                .get("profileImage", String.class);
    }

    public String getTelNumber(String token) {
        return getPayload(token)
                .get("telNumber", String.class);
    }

    public String getRole(String token) {
        return getPayload(token)
                .get("role", String.class);
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

    public Boolean getIsVerified(String token) {
        return getPayload(token)
                .get("isVerified", Boolean.class);
    }


    public String getBearerTokenToString(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.split(" ")[1];
        }
        return null;
    }

    public void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh", "Bearer " + tokenDto.getRefreshToken());
    }
}
