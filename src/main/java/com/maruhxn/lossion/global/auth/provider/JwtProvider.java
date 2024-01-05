package com.maruhxn.lossion.global.auth.provider;

import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private SecretKey secretKey;
    private JwtParser jwtParser;

    public JwtProvider(@Value("${jwt.secret-key}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    public String createJwt(JwtMemberInfo jwtMemberInfo) {
        Long expiredMs = 15 * 1000L;
        String role = jwtMemberInfo.getRole();

        return Jwts.builder()
                .claim("accountId", jwtMemberInfo.getAccountId())
                .claim("email", jwtMemberInfo.getEmail())
                .claim("username", jwtMemberInfo.getUsername())
                .claim("telNumber", jwtMemberInfo.getTelNumber())
                .claim("profileImage", jwtMemberInfo.getProfileImage())
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    private Claims getPayload(String token) {
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

    public Boolean isExpired(String token) {
        return getPayload(token)
                .getExpiration()
                .before(new Date());
    }

}
