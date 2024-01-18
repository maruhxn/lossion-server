package com.maruhxn.lossion.domain.member.application;

import com.maruhxn.lossion.domain.auth.dao.RefreshTokenRepository;
import com.maruhxn.lossion.domain.auth.domain.RefreshToken;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.member.dto.request.UpdateMemberProfileReq;
import com.maruhxn.lossion.domain.member.dto.request.UpdatePasswordReq;
import com.maruhxn.lossion.domain.member.dto.response.ProfileItem;
import com.maruhxn.lossion.global.auth.application.JwtUtils;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.util.IntegrationTestSupport;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("[서비스] - MemberService")
class MemberServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @DisplayName("회원 프로필을 조회할 수 있다.")
    @Test
    void getProfile() {
        // Given
        Member member = createMember("tester", "tester", "test@test.com", "01000000000");

        // When
        ProfileItem profile = memberService.getProfile(member.getId());

        // Then
        assertThat(profile)
                .extracting("accountId", "email", "username", "telNumber", "isVerified", "profileImage")
                .contains("tester", "tester", "test@test.com", "01000000000", false, "defaultProfileImage.jfif");

    }

    @DisplayName("회원 프로필을 조회 시 존재하지 않는 회원의 아이디를 전달하면 에러를 발생한다.")
    @Test
    void getProfileWithNonExistingMemberId() {
        // When / Then
        assertThatThrownBy(() -> memberService.getProfile(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());

    }

    @DisplayName("회원 이름을 수정할 수 있다.")
    @Test
    void updateProfileWithUsername() {
        // Given
        Member member = createMember("tester", "tester", "test@test.com", "01000000000");
        UpdateMemberProfileReq req = UpdateMemberProfileReq.builder()
                .username("tester!")
                .build();

        // When
        memberService.updateProfile(member.getId(), req);

        // Then
        assertThat(member.getUsername()).isEqualTo("tester!");

    }

    @DisplayName("회원 이름을 수정 시 이미 존재하는 이름의 경우 에러를 반환한다.")
    @Test
    void updateProfileWithExistingUsername() {
        // Given
        Member member1 = createMember("tester1", "tester1", "test1@test.com", "01000000000");
        createMember("tester2", "tester2", "test2@test.com", "01000000001");

        UpdateMemberProfileReq req = UpdateMemberProfileReq.builder()
                .username("tester2")
                .build();

        // When / Then
        memberService.updateProfile(member1.getId(), req);
        assertThatThrownBy(() -> em.flush())
                .isInstanceOf(ConstraintViolationException.class);

    }

    @DisplayName("회원 이메일을 수정할 수 있다.")
    @Test
    void updateProfileWithEmail() {
        // Given
        Member member = createMember("tester", "tester", "test@test.com", "01000000000");
        UpdateMemberProfileReq req = UpdateMemberProfileReq.builder()
                .username("tester!")
                .build();

        // When
        memberService.updateProfile(member.getId(), req);

        // Then
        assertThat(member.getUsername()).isEqualTo("tester!");

    }

    @DisplayName("회원 이메일을 수정 시 이미 존재하는 이메일의 경우 에러를 반환한다.")
    @Test
    void updateProfileWithExistingEmail() {
        // Given
        Member member1 = createMember("tester1", "tester1", "test1@test.com", "01000000000");
        createMember("tester2", "tester2", "test2@test.com", "01000000001");

        UpdateMemberProfileReq req = UpdateMemberProfileReq.builder()
                .email("test2@test.com")
                .build();

        // When / Then
        memberService.updateProfile(member1.getId(), req);
        assertThatThrownBy(() -> em.flush())
                .isInstanceOf(ConstraintViolationException.class);

    }

    @DisplayName("회원 프로필 이미지를 수정할 수 있다.")
    @Test
    void updateProfileWithProfileImage() throws IOException {
        // Given
        Member member = createMember("tester", "tester", "test@test.com", "01000000000");
        MockMultipartFile newProfileImage = getMockMultipartFile();
        UpdateMemberProfileReq req = UpdateMemberProfileReq.builder()
                .profileImage(newProfileImage)
                .build();
        given(fileService.saveAndExtractUpdatedProfileImage(any(MultipartFile.class)))
                .willReturn("newProfileImageName");
        // When
        memberService.updateProfile(member.getId(), req);

        // Then
        assertThat(member.getProfileImage()).isNotEqualTo("defaultProfileImage.jfif");

    }

    @DisplayName("회원 정보 수정 시 존재하지 않는 회원의 아이디를 전달하면 에러를 발생한다.")
    @Test
    void updateProfileWithNonExistingMemberId() {
        UpdateMemberProfileReq req = UpdateMemberProfileReq.builder()
                .username("username")
                .build();
        // When / Then
        assertThatThrownBy(() -> memberService.updateProfile(1L, req))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());

    }

    @DisplayName("회원 정보 수정 시 어떠한 값도 전달하지 않으면 에러를 발생한다.")
    @Test
    void updateProfileWithoutData() {
        UpdateMemberProfileReq req = UpdateMemberProfileReq.builder()
                .build();
        // When / Then
        assertThatThrownBy(() -> memberService.updateProfile(1L, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.BAD_REQUEST.getMessage());

    }

    @DisplayName("회원 비밀번호를 수정할 수 있다.")
    @Test
    void updatePassword() {
        // Given
        Member member = createMember("tester", "tester", "test@test.com", "01000000000");
        UpdatePasswordReq req = UpdatePasswordReq.builder()
                .currPassword("test")
                .newPassword("test!!")
                .confirmNewPassword("test!!")
                .build();

        // When
        memberService.updatePassword(member.getId(), req);

        // Then
        assertThat(passwordEncoder.matches("test!!", member.getPassword())).isTrue();

    }

    @DisplayName("회원 비밀번호를 수정 시 현재 비밀번호가 올바르지 않으면 에러를 반환한다.")
    @Test
    void updatePasswordWithIncorrectPassword() {
        // Given
        Member member = createMember("tester", "tester", "test@test.com", "01000000000");
        UpdatePasswordReq req = UpdatePasswordReq.builder()
                .currPassword("hack")
                .newPassword("test!!")
                .confirmNewPassword("test!!")
                .build();

        // When / Then
        assertThatThrownBy(() -> memberService.updatePassword(member.getId(), req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INCORRECT_PASSWORD.getMessage());

    }

    @DisplayName("회원 비밀번호를 수정 시 새로운 비밀번호와 비밀번호 확인이 올바르지 않으면 에러를 반환한다.")
    @Test
    void updatePasswordWhenConfirmFail() {
        // Given
        Member member = createMember("tester", "tester", "test@test.com", "01000000000");
        UpdatePasswordReq req = UpdatePasswordReq.builder()
                .currPassword("test")
                .newPassword("test!")
                .confirmNewPassword("test!!")
                .build();

        // When / Then
        assertThatThrownBy(() -> memberService.updatePassword(member.getId(), req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.PASSWORD_CONFIRM_FAIL.getMessage());

    }

    @DisplayName("회원 비밀번호를 수정 시 기존 비밀번호와 일치하면 에러를 반환한다.")
    @Test
    void updatePasswordWithSamePassword() {
        // Given
        Member member = createMember("tester", "tester", "test@test.com", "01000000000");
        UpdatePasswordReq req = UpdatePasswordReq.builder()
                .currPassword("test")
                .newPassword("test")
                .confirmNewPassword("test")
                .build();

        // When / Then
        assertThatThrownBy(() -> memberService.updatePassword(member.getId(), req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.SAME_PASSWORD.getMessage());

    }

    @DisplayName("비밀번호 수정 시 존재하지 않는 회원의 아이디를 전달하면 에러를 발생한다.")
    @Test
    void updatePasswordWithNonExistingMemberId() {
        UpdatePasswordReq req = UpdatePasswordReq.builder()
                .currPassword("test")
                .newPassword("test!!")
                .confirmNewPassword("test!!")
                .build();

        // When / Then
        assertThatThrownBy(() -> memberService.updatePassword(1L, req))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());

    }

    @DisplayName("회원 탈퇴가 가능하다.")
    @Test
    void membershipWithdrawal() {
        // Given
        Member member = createMember("tester", "tester", "test@test,com", "01000000000");
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);

        String rawRefreshToken = jwtUtils.generateRefreshToken(jwtMemberInfo);
        RefreshToken refreshToken = RefreshToken.builder()
                .email(member.getEmail())
                .accountId(member.getAccountId())
                .refreshToken(rawRefreshToken)
                .build();
        refreshTokenRepository.save(refreshToken);

        // When
        memberService.membershipWithdrawal(member.getId());

        // Then
        Optional<Member> optionalMember = memberRepository.findById(member.getId());
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(optionalMember.isEmpty()).isTrue();
        assertThat(refreshTokens).isEmpty();
    }

    @DisplayName("회원 탈퇴 시 존재하지 않는 회원의 아이디를 전달하면 에러를 발생한다..")
    @Test
    void membershipWithdrawalWithNonExistingMemberId() {
        assertThatThrownBy(() -> memberService.membershipWithdrawal(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    private Member createMember(String accountId, String username, String email, String telNumber) {
        Member member = Member.builder()
                .accountId(accountId)
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("test"))
                .telNumber(telNumber)
                .build();

        return memberRepository.save(member);
    }

    private MockMultipartFile getMockMultipartFile() throws IOException {
        final String fileName = "defaultProfileImage"; // 파일명
        final String contentType = "jfif"; // 파일타입
        final String filePath = "src/test/resources/static/img/" + fileName + "." + contentType; //파일경로

        return new MockMultipartFile(
                "images", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                new FileInputStream(filePath)
        );
    }
}