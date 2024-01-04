package com.maruhxn.lossion.global.auth.provider;

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

    public JwtProvider(@Value("${jwt.secret-key}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createJwt(String accountId, String email, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("acountId", accountId)
                .claim("email", email)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String getAccountId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 검증 진행
                .build() //JwtParser
                .parseSignedClaims(token)
                .getPayload()
                .get("accountId", String.class);
    }

    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 검증 진행
                .build() //JwtParser
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 검증 진행
                .build() //JwtParser
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

}
