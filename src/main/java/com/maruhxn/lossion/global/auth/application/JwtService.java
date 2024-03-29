package com.maruhxn.lossion.global.auth.application;

import com.maruhxn.lossion.domain.auth.dao.RefreshTokenRepository;
import com.maruhxn.lossion.domain.auth.domain.RefreshToken;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.dto.TokenDto;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.global.error.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtService {

    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public void saveRefreshToken(JwtMemberInfo jwtMemberInfo, TokenDto tokenDto) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountId(jwtMemberInfo.getAccountId());
        refreshToken.ifPresentOrElse(
                // 있다면 새토큰 발급후 업데이트
                token -> {
                    token.updateToken(tokenDto.getRefreshToken());
                },
                // 없다면 새로 만들고 DB에 저장
                () -> {
                    RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), jwtMemberInfo.getAccountId());
                    refreshTokenRepository.save(newToken);
                });
    }

    public TokenDto refresh(
            String bearerRefreshToken,
            HttpServletResponse response
    ) {
        String refreshToken = jwtUtils.getBearerTokenToString(bearerRefreshToken);

        if (!jwtUtils.validate(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }

        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_REFRESH_TOKEN));

        Member findMember = memberRepository.findByAccountId(findRefreshToken.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        // access token 과 refresh token 모두를 재발급
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(findMember);
        String newAccessToken = jwtUtils.generateAccessToken(jwtMemberInfo, new Date());
        String newRefreshToken = jwtUtils.generateRefreshToken(jwtMemberInfo, new Date());

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        this.saveRefreshToken(jwtMemberInfo, tokenDto);

        jwtUtils.setHeader(response, tokenDto);

        return tokenDto;
    }

    public void logout(String bearerRefreshToken) {
        String refreshToken = jwtUtils.getBearerTokenToString(bearerRefreshToken);
        String accountId = jwtUtils.getPayload(refreshToken).getSubject();
        refreshTokenRepository.deleteAllByAccountId(accountId);
        // TODO: 블랙리스트 처리하여, 이미 삭제된 accessToken으로는 로그인을 할 수 없게 처리. -> Redis 혹은 DB에 accessToken을 저장해야함.
    }
}
