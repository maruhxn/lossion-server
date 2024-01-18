package com.maruhxn.lossion.domain.auth.application;

import com.maruhxn.lossion.domain.auth.dao.AuthTokenRepository;
import com.maruhxn.lossion.domain.auth.domain.AuthToken;
import com.maruhxn.lossion.domain.auth.dto.*;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.member.dto.request.UpdateAnonymousPasswordReq;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.AlreadyExistsResourceException;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.global.error.exception.ExpirationException;
import com.maruhxn.lossion.global.util.AesUtil;
import com.maruhxn.lossion.infra.EmailService;
import com.maruhxn.lossion.util.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.maruhxn.lossion.domain.member.domain.Role.ROLE_USER;
import static com.maruhxn.lossion.global.common.Constants.BASIC_PROFILE_IMAGE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("[서비스] - AuthService")
class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입")
    void signUp() {
        // given
        SignUpReq req = SignUpReq.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password("test")
                .confirmPassword("test")
                .telNumber("01000000000")
                .build();

        // then
        authService.signUp(req);

        // when
        List<Member> members = memberRepository.findAll();
        assertThat(members.get(0))
                .extracting("accountId", "email", "telNumber", "username", "profileImage", "role", "isVerified")
                .contains("tester", "test@test.com", "01000000000", "tester", BASIC_PROFILE_IMAGE_NAME, ROLE_USER, false);
    }

    @DisplayName("회원가입 시 이미 존재하는 아이디로 요청하는 경우 에러를 반환한다.")
    @Test
    void signUpWithExistingAccountId() {
        // Given
        Member member = createMember();

        SignUpReq req = SignUpReq.builder()
                .accountId("tester")
                .username("tester2")
                .email("test2@test.com")
                .password("test")
                .confirmPassword("test")
                .telNumber("01000000001")
                .build();

        // When / Then
        assertThatThrownBy(() -> authService.signUp(req))
                .isInstanceOf(AlreadyExistsResourceException.class)
                .hasMessage(ErrorCode.EXISTING_ID.getMessage());
    }

    @DisplayName("회원가입 시 이미 존재하는 이메일로 요청하는 경우 에러를 반환한다.")
    @Test
    void signUpWithExistingEmail() {
        // Given
        Member member = createMember();

        SignUpReq req = SignUpReq.builder()
                .accountId("tester2")
                .username("tester2")
                .email("test@test.com")
                .password("test")
                .confirmPassword("test")
                .telNumber("01000000001")
                .build();

        // When / Then
        assertThatThrownBy(() -> authService.signUp(req))
                .isInstanceOf(AlreadyExistsResourceException.class)
                .hasMessage(ErrorCode.EXISTING_EMAIL.getMessage());
    }

    @DisplayName("회원가입 시 이미 존재하는 유저명으로 요청하는 경우 에러를 반환한다.")
    @Test
    void signUpWithExistingUsername() {
        // Given
        Member member = createMember();

        SignUpReq req = SignUpReq.builder()
                .accountId("tester2")
                .username("tester")
                .email("test2@test.com")
                .password("test")
                .confirmPassword("test")
                .telNumber("01000000001")
                .build();

        // When / Then
        assertThatThrownBy(() -> authService.signUp(req))
                .isInstanceOf(AlreadyExistsResourceException.class)
                .hasMessage(ErrorCode.EXISTING_USERNAME.getMessage());
    }

    @DisplayName("회원가입 시 이미 존재하는 전화번호로 요청하는 경우 에러를 반환한다.")
    @Test
    void signUpWithExistingTelNumber() {
        // Given
        Member member = createMember();

        SignUpReq req = SignUpReq.builder()
                .accountId("tester2")
                .username("tester2")
                .email("test2@test.com")
                .password("test")
                .confirmPassword("test")
                .telNumber("01000000000")
                .build();

        // When / Then
        assertThatThrownBy(() -> authService.signUp(req))
                .isInstanceOf(AlreadyExistsResourceException.class)
                .hasMessage(ErrorCode.EXISTING_TEL.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호와 비밀번호 확인이 일치하지 않는 경우 에러를 반환한다.")
    @Test
    void signUpWithNonMatchingPassword() {
        // Given
        SignUpReq req = SignUpReq.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password("test")
                .confirmPassword("testt")
                .telNumber("01000000000")
                .build();

        // When / Then
        assertThatThrownBy(() -> authService.signUp(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.PASSWORD_CONFIRM_FAIL.getMessage());
    }

    private Member createMember() {
        Member member = Member.builder()
                .accountId("tester")
                .email("test@test.com")
                .telNumber("01000000000")
                .username("tester")
                .password(passwordEncoder.encode("test"))
                .build();

        return memberRepository.save(member);
    }

    @Test
    @DisplayName("로그인 한 사용자는 이메일 인증을 위해 인증 메일 발송을 요청할 수 있다.")
    void sendVerifyEmailWithLogin() {
        // given
        Member member = createMember();
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        willDoNothing().given(emailService).sendEmail(anyString(), anyString());

        // when
        authService.sendVerifyEmailWithLogin(member, now);

        // then
        List<AuthToken> tokens = authTokenRepository.findAll();
        assertThat(tokens.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 이메일 인증된 사용자는 이미 인증되었다는 에러를 반환한다.")
    void sendVerifyEmailWithAlreadyVerified() {
        // given
        Member member = createMember();
        member.verifyEmail();
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        // when / then
        assertThatThrownBy(() -> authService.sendVerifyEmailWithLogin(member, now))
                .isInstanceOf(AlreadyExistsResourceException.class)
                .hasMessage(ErrorCode.ALREADY_VERIFIED.getMessage());
    }

    @DisplayName("익명 사용자의 경우 비밀번호 찾기 등을 통해 인증 메일을 요청할 수 있다.")
    @Test
    void sendVerifyEmailWithAnonymous() {
        // Given
        Member member = createMember();
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        SendAnonymousEmailReq req = SendAnonymousEmailReq.builder()
                .accountId("tester")
                .email("test@test.com")
                .build();

        // When
        authService.sendVerifyEmailWithAnonymous(req, now);

        // Then
        List<AuthToken> tokens = authTokenRepository.findAll();
        assertThat(tokens.size()).isEqualTo(1);
    }

    @DisplayName("익명 사용자의 인증 메일을 요청 시 요청 정보와 일치하는 유저가 없을 경우, 에러를 반환한다.")
    @Test
    void sendVerifyEmailWithAnonymousWithNonExistingUserData() {
        // Given
        Member member = createMember();
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        SendAnonymousEmailReq req = SendAnonymousEmailReq.builder()
                .accountId("tester!")
                .email("test@test.com")
                .build();

        // When / Then
        assertThatThrownBy(() -> authService.sendVerifyEmailWithAnonymous(req, now))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @DisplayName("인증 메일에 포함된 payload 값을 전달함으로써 본인의 이메일을 인증할 수 있다.")
    @Test
    void verifyEmail() {
        // Given
        Member member = createMember();
        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        AuthToken authToken = createAuthToken(payload, now, member);

        VerifyEmailReq req = new VerifyEmailReq(payload);

        // When
        authService.verifyEmail(member, req, now);

        // Then
        List<AuthToken> tokens = authTokenRepository.findAll();
        assertThat(tokens.isEmpty()).isTrue();
        assertThat(member.getIsVerified()).isTrue();
    }

    @DisplayName("이미 인증된 사용자는 이메일 인증 시도를 할 수 없다.")
    @Test
    void verifyEmailWithAlreadyVerified() {
        // Given
        Member member = createMember();
        member.verifyEmail();

        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        VerifyEmailReq req = new VerifyEmailReq(payload);

        // When / Then
        assertThatThrownBy(() -> authService.verifyEmail(member, req, now))
                .isInstanceOf(AlreadyExistsResourceException.class)
                .hasMessage(ErrorCode.ALREADY_VERIFIED.getMessage());

    }

    @DisplayName("이미 만료된 토큰을 전달하면 이메일 인증은 실패한다.")
    @Test
    void verifyEmailWithInvalidAuthToken() {
        // Given
        Member member = createMember();
        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        AuthToken authToken = AuthToken.builder()
                .payload(payload)
                .expiredAt(now.minusSeconds(5))
                .member(member)
                .build();
        authTokenRepository.save(authToken);

        VerifyEmailReq req = new VerifyEmailReq(payload);

        // When / Then
        assertThatThrownBy(() -> authService.verifyEmail(member, req, now))
                .isInstanceOf(ExpirationException.class)
                .hasMessage(ErrorCode.TOKEN_EXPIRATION.getMessage());

    }

    @DisplayName("비밀번호 인증에 성공하면 어떠한 값도 반환하지 않는다.")
    @Test
    void verifyPassword() {
        // Given
        Member member = createMember();
        VerifyPasswordReq req = new VerifyPasswordReq("test");

        // When
        authService.verifyPassword(member, req);
    }

    @DisplayName("비밀번호 인증 시 비밀번호가 일치하지 않으면 에러를 반환한다.")
    @Test
    void verifyPasswordFail() {
        // Given
        Member member = createMember();
        VerifyPasswordReq req = new VerifyPasswordReq("test!");

        // When / Then
        assertThatThrownBy(() -> authService.verifyPassword(member, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.PASSWORD_CONFIRM_FAIL.getMessage());
    }

    @DisplayName("익명 사용자는 [이메일 인증 이후], 아이디와 이메일, 발급받은 이메일 인증 토큰을 통해 비밀번호 변경에 사용할 [인증키]를 발급받을 수 있다.")
    @Test
    void getTokenToFindPassword() {
        // Given
        ReflectionTestUtils.setField(AesUtil.class, "privateKey", "verySecretKey");
        ReflectionTestUtils.setField(AesUtil.class, "privateIv", "1234123412341234");

        Member member = createMember();
        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        AuthToken authToken = createAuthToken(payload, now, member);

        GetTokenReq req = GetTokenReq.builder()
                .accountId("tester")
                .email("test@test.com")
                .payload(payload)
                .build();

        // When
        String token = authService.getAuthKeyToFindPassword(req, now);

        // Then
        assertThat(AesUtil.decrypt(token)).isEqualTo(payload);
    }

    @DisplayName("익명 사용자가 비밀번호 변경에 사용할 인증 키를 발급 요청 시, 요청 정보에 해당하는 유저가 없을 경우 에러를 반환한다.")
    @Test
    void getTokenToFindPasswordWithNonExistingMemberData() {
        // Given
        Member member = createMember();
        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        AuthToken authToken = createAuthToken(payload, now, member);

        GetTokenReq req = GetTokenReq.builder()
                .accountId("tester1")
                .email("test@test.com")
                .payload(payload)
                .build();

        // When / Then
        assertThatThrownBy(() -> authService.getAuthKeyToFindPassword(req, now))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @DisplayName("익명 사용자가 비밀번호 변경에 사용할 인증 키를 발급 요청 시, payload에 해당하는 인증 토큰이 없을 경우 에러를 반환한다.")
    @Test
    void getTokenToFindPasswordWithNonExistingTokenPayload() {
        // Given
        Member member = createMember();
        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        GetTokenReq req = GetTokenReq.builder()
                .accountId("tester")
                .email("test@test.com")
                .payload(payload)
                .build();

        // When / Then
        assertThatThrownBy(() -> authService.getAuthKeyToFindPassword(req, now))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_TOKEN.getMessage());
    }

    @DisplayName("익명 사용자가 비밀번호 변경에 사용할 인증 키를 발급 요청 시, 조회된 인증 토큰이 만료되었을 경우 에러를 반환한다.")
    @Test
    void getTokenToFindPasswordWithInvalidTokenPayload() {
        // Given
        Member member = createMember();
        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        AuthToken authToken = AuthToken.builder()
                .payload(payload)
                .expiredAt(now.minusSeconds(5))
                .member(member)
                .build();
        authTokenRepository.save(authToken);

        GetTokenReq req = GetTokenReq.builder()
                .accountId("tester")
                .email("test@test.com")
                .payload(payload)
                .build();

        // When / Then
        assertThatThrownBy(() -> authService.getAuthKeyToFindPassword(req, now))
                .isInstanceOf(ExpirationException.class)
                .hasMessage(ErrorCode.TOKEN_EXPIRATION.getMessage());
    }

    private AuthToken createAuthToken(String payload, LocalDateTime now, Member member) {
        AuthToken authToken = AuthToken.builder()
                .payload(payload)
                .expiredAt(now.plusMinutes(5))
                .member(member)
                .build();
        return authTokenRepository.save(authToken);
    }

    @DisplayName("발급받은 인증키와 비밀번호 변경 정보를 전달하면, 비밀번호를 변경할 수 있다.")
    @Test
    void updateAnonymousPassword() {
        // Given
        ReflectionTestUtils.setField(AesUtil.class, "privateKey", "verySecretKey");
        ReflectionTestUtils.setField(AesUtil.class, "privateIv", "1234123412341234");

        Member member = createMember();
        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        AuthToken authToken = createAuthToken(payload, now, member);
        String authKey = AesUtil.encrypt(authToken.getPayload());

        UpdateAnonymousPasswordReq req = UpdateAnonymousPasswordReq.builder()
                .newPassword("test!")
                .confirmNewPassword("test!")
                .build();

        // When
        authService.updateAnonymousPassword(authKey, req);

        // Then
        List<AuthToken> tokens = authTokenRepository.findAll();
        assertThat(passwordEncoder.matches("test!", member.getPassword())).isTrue();
        assertThat(tokens).isEmpty();

    }

    @DisplayName("비밀번호 변경 시, authKey와 대응되는 authToken이 없다면 에러를 반환한다.")
    @Test
    void updateAnonymousPasswordFailWhenNonMatchingAuthToken() {
        // Given
        ReflectionTestUtils.setField(AesUtil.class, "privateKey", "verySecretKey");
        ReflectionTestUtils.setField(AesUtil.class, "privateIv", "1234123412341234");

        String authKey = AesUtil.encrypt("nonMatchingAuthKey");

        UpdateAnonymousPasswordReq req = UpdateAnonymousPasswordReq.builder()
                .newPassword("test!")
                .confirmNewPassword("test!")
                .build();

        // When
        assertThatThrownBy(() -> authService.updateAnonymousPassword(authKey, req))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_TOKEN.getMessage());

    }

    @DisplayName("비밀번호 변경 시, 비밀번호가 서로 일치하지 않는다면 에러를 반환한다.")
    @Test
    void updateAnonymousPasswordFailWhenNonMatchingPassword() {
        // Given
        ReflectionTestUtils.setField(AesUtil.class, "privateKey", "verySecretKey");
        ReflectionTestUtils.setField(AesUtil.class, "privateIv", "1234123412341234");

        Member member = createMember();
        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);

        AuthToken authToken = createAuthToken(payload, now, member);
        String authKey = AesUtil.encrypt(authToken.getPayload());

        UpdateAnonymousPasswordReq req = UpdateAnonymousPasswordReq.builder()
                .newPassword("test!")
                .confirmNewPassword("test!!")
                .build();

        // When
        assertThatThrownBy(() -> authService.updateAnonymousPassword(authKey, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.PASSWORD_CONFIRM_FAIL.getMessage());

    }
}