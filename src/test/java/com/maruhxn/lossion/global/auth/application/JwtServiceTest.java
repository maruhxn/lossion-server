package com.maruhxn.lossion.global.auth.application;

import com.maruhxn.lossion.domain.auth.dao.RefreshTokenRepository;
import com.maruhxn.lossion.domain.auth.domain.RefreshToken;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.dto.TokenDto;
import com.maruhxn.lossion.global.common.Constants;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.util.IntegrationTestSupport;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Service] - JwtService")
class JwtServiceTest extends IntegrationTestSupport {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @DisplayName("jwt의 payload를 바탕으로 조회한 refreshToken이 없다면 새롭게 저장한다.")
    @Test
    void saveRefreshToken1() {
        // Given
        JwtMemberInfo jwtMemberInfo = createJwtMemberInfo();

        TokenDto tokenDto = TokenDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        // When
        jwtService.saveRefreshToken(jwtMemberInfo, tokenDto);

        // Then
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByAccountId(jwtMemberInfo.getAccountId());
        assertThat(optionalRefreshToken.isPresent()).isTrue();

    }

    @DisplayName("jwt의 payload를 바탕으로 조회한 refreshToken이 있다면 전달받은 값으로 덮어씌운다.")
    @Test
    void saveRefreshToken2() {
        // Given
        RefreshToken refreshToken = RefreshToken.builder()
                .accountId("tester")
                .refreshToken("refreshToken")
                .build();
        refreshTokenRepository.save(refreshToken);

        JwtMemberInfo jwtMemberInfo = createJwtMemberInfo();

        TokenDto tokenDto = TokenDto.builder()
                .accessToken("accessToken")
                .refreshToken("newRefreshToken")
                .build();

        // When
        jwtService.saveRefreshToken(jwtMemberInfo, tokenDto);

        // Then
        RefreshToken findRefreshToken = refreshTokenRepository.findByAccountId(jwtMemberInfo.getAccountId()).get();
        assertThat(findRefreshToken.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());

    }

    @DisplayName("refreshToken이 유효하다면 accessToken과 refreshToken을 새로 발급한다.")
    @Test
    void refresh() {
        // Given
        HttpServletResponse response = new MockHttpServletResponse();
        Member member = createMember();
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);

        String rawRefreshToken = jwtUtils.generateRefreshToken(jwtMemberInfo, new Date());
        RefreshToken refreshToken = RefreshToken.builder()
                .accountId(member.getAccountId())
                .refreshToken(rawRefreshToken)
                .build();
        refreshTokenRepository.save(refreshToken);

        String bearerRefreshToken = Constants.BEARER_PREFIX + rawRefreshToken;
        // When
        TokenDto tokenDto = jwtService.refresh(bearerRefreshToken, response);
        // Then
        assertThat(response.getHeader(Constants.ACCESS_TOKEN_HEADER)).isEqualTo(Constants.BEARER_PREFIX + tokenDto.getAccessToken());
        assertThat(response.getHeader(Constants.REFRESH_TOKEN_HEADER)).isEqualTo(Constants.BEARER_PREFIX + tokenDto.getRefreshToken());
    }

    @DisplayName("refreshToken이 만료되었다면 401 에러를 반환한다.")
    @Test
    void refreshWithInvalidRefreshToken() {
        // Given
        HttpServletResponse response = new MockHttpServletResponse();
        Member member = createMember();
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 10, 0);
        String rawRefreshToken = jwtUtils.generateRefreshToken(jwtMemberInfo, Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));

        RefreshToken refreshToken = RefreshToken.builder()
                .accountId(member.getAccountId())
                .refreshToken(rawRefreshToken)
                .build();
        refreshTokenRepository.save(refreshToken);

        String bearerRefreshToken = Constants.BEARER_PREFIX + rawRefreshToken;
        // When / Then
        assertThatThrownBy(() -> jwtService.refresh(bearerRefreshToken, response))
                .isInstanceOf(JwtException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @DisplayName("refreshToken이 데이터베이스 존재하지 않다면 에러를 반환한다.")
    @Test
    void refreshFailWhenNoRefreshToken() {
        // Given
        HttpServletResponse response = new MockHttpServletResponse();
        Member member = createMember();
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);

        String rawRefreshToken = jwtUtils.generateRefreshToken(jwtMemberInfo, new Date());

        String bearerRefreshToken = Constants.BEARER_PREFIX + rawRefreshToken;
        // When / Then
        assertThatThrownBy(() -> jwtService.refresh(bearerRefreshToken, response))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_REFRESH_TOKEN.getMessage());
    }

    @DisplayName("refreshToken에 대응되는 유저 정보가 데이터베이스 존재하지 않다면 에러를 반환한다.")
    @Test
    void refreshFailWhenNoMember() {
        // Given
        HttpServletResponse response = new MockHttpServletResponse();
        JwtMemberInfo jwtMemberInfo = createJwtMemberInfo();

        String rawRefreshToken = jwtUtils.generateRefreshToken(jwtMemberInfo, new Date());

        RefreshToken refreshToken = RefreshToken.builder()
                .accountId(jwtMemberInfo.getAccountId())
                .refreshToken(rawRefreshToken)
                .build();
        refreshTokenRepository.save(refreshToken);

        String bearerRefreshToken = Constants.BEARER_PREFIX + rawRefreshToken;
        // When / Then
        assertThatThrownBy(() -> jwtService.refresh(bearerRefreshToken, response))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @DisplayName("refreshToken 헤더 정보를 받아 해당 정보와 관련된 refreshToken을 모두 삭제하여 철저히 로그아웃 한다.")
    @Test
    void logout() {
        // Given
        Member member = createMember();
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);

        String rawRefreshToken = jwtUtils.generateRefreshToken(jwtMemberInfo, new Date());
        RefreshToken refreshToken = RefreshToken.builder()
                .accountId(member.getAccountId())
                .refreshToken(rawRefreshToken)
                .build();
        refreshTokenRepository.save(refreshToken);

        String bearerRefreshToken = Constants.BEARER_PREFIX + rawRefreshToken;
        // When
        jwtService.logout(bearerRefreshToken);

        // Then
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).isEmpty();

    }

    private static JwtMemberInfo createJwtMemberInfo() {
        return JwtMemberInfo.builder()
                .id(1L)
                .accountId("tester")
                .build();
    }

    private Member createMember() {
        Member member = Member.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .telNumber("01000000000")
                .password("test")
                .build();
        return memberRepository.save(member);
    }
}