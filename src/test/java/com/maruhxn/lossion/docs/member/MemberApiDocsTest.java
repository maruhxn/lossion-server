package com.maruhxn.lossion.docs.member;

import com.maruhxn.lossion.domain.member.dto.request.UpdateMemberProfileReq;
import com.maruhxn.lossion.domain.member.dto.request.UpdatePasswordReq;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import com.maruhxn.lossion.global.common.Constants;
import com.maruhxn.lossion.util.RestDocsSupport;
import com.maruhxn.lossion.util.WithMockCustomOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maruhxn.lossion.global.common.Constants.ACCESS_TOKEN_HEADER;
import static com.maruhxn.lossion.global.common.Constants.REFRESH_TOKEN_HEADER;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Docs] - MemberAPIDocs")
public class MemberApiDocsTest extends RestDocsSupport {

    final String MEMBER_API_PATH = "/api/members/{memberId}";

    @Test
    @DisplayName("Member 프로필 조회")
    void getProfile() throws Exception {
        getAction(MEMBER_API_PATH, true, null, member.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("프로필 조회 성공"))
                .andExpect(jsonPath("$.data.accountId").value("tester"))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.username").value("tester"))
                .andExpect(jsonPath("$.data.isVerified").value(true))
                .andExpect(jsonPath("$.data.profileImage").value(Constants.BASIC_PROFILE_IMAGE_NAME))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("memberId").description("사용자 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                commonResponseFields("ProfileItem")
                                        .andWithPrefix("data.",
                                                fieldWithPath("accountId").type(STRING).description("사용자 계정 ID"),
                                                fieldWithPath("email").type(STRING).description("이메일"),
                                                fieldWithPath("username").type(STRING).description("유저명"),
                                                fieldWithPath("isVerified").type(BOOLEAN).description("이메일 인증 여부"),
                                                fieldWithPath("profileImage").type(STRING).description("프로필 이미지")
                                        )
                        )
                );
    }

    @Test
    @DisplayName("OAuth2 Member 프로필 조회")
    @WithMockCustomOAuth2User(registrationId = "google")
    void getProfileWithOAuth2User() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        getAction(MEMBER_API_PATH, false, null, userDetails.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("프로필 조회 성공"))
                .andExpect(jsonPath("$.data.accountId").value("google_111111111111111111111"))
                .andExpect(jsonPath("$.data.email").value("oauth@test.com"))
                .andExpect(jsonPath("$.data.username").value("oauth"))
                .andExpect(jsonPath("$.data.isVerified").value(true))
                .andExpect(jsonPath("$.data.profileImage").value("https://test_profile_image.com"));
    }

    @Test
    @DisplayName("Member 프로필 수정")
    void updateProfile() throws Exception {
        // Given
        MockMultipartFile profileImage = getMockMultipartFile();
        Map<String, String> parts = new HashMap<>();
        parts.put("username", "username");
        parts.put("email", "test!@test.com");

        // When / Then
        multipartPatchAction(MEMBER_API_PATH, UpdateMemberProfileReq.class, List.of(profileImage), true, parts, member.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("memberId").description("사용자 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestParts(
                                        partWithName("username").optional().description("수정할 유저명")
                                                .attributes(withPath("username")),
                                        partWithName("email").optional().description("수정할 이메일")
                                                .attributes(withPath("email")),
                                        partWithName("profileImage").optional().description("수정할 프로필 이미지")
                                                .attributes(withPath("profileImage"))
                                )
                        )
                );

    }

    @Test
    @DisplayName("OAuth2 Member 프로필 수정")
    @WithMockCustomOAuth2User(registrationId = "google")
    void updateProfileWithOAuth2User() throws Exception {
        // Given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        MockMultipartFile profileImage = getMockMultipartFile();
        Map<String, String> parts = new HashMap<>();
        parts.put("username", "username");
        parts.put("email", "test!@test.com");

        // When / Then
        multipartPatchAction(MEMBER_API_PATH, UpdateMemberProfileReq.class, List.of(profileImage), false, parts, userDetails.getId())
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Member 비밀번호 변경")
    void updatePassword() throws Exception {
        // Given
        UpdatePasswordReq req = UpdatePasswordReq.builder()
                .currPassword("test")
                .newPassword("updatedTest")
                .confirmNewPassword("updatedTest")
                .build();

        // When / Then
        patchAction(MEMBER_API_PATH + "/password", req, true, null, member.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("memberId").description("사용자 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestFields(
                                        fieldWithPath("currPassword").type(STRING).description("현재 비밀번호")
                                                .attributes(withPath("currPassword")),
                                        fieldWithPath("newPassword").type(STRING).description("새 비밀번호")
                                                .attributes(withPath("newPassword")),
                                        fieldWithPath("confirmNewPassword").type(STRING).description("새 비밀번호 확인")
                                                .attributes(withPath("confirmNewPassword"))
                                )
                        )
                );
    }

    @Test
    @DisplayName("OAuth2 Member의 비밀번호 변경 시도 시 400 반환")
    @WithMockCustomOAuth2User(registrationId = "google")
    void updatePasswordWithOAuth2User() throws Exception {
        // Given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UpdatePasswordReq req = UpdatePasswordReq.builder()
                .currPassword("test")
                .newPassword("updatedTest")
                .confirmNewPassword("updatedTest")
                .build();

        // When / Then
        patchAction(MEMBER_API_PATH + "/password", req, false, null, userDetails.getId())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Member 회원 탈퇴")
    void withdraw() throws Exception {
        deleteAction(MEMBER_API_PATH, true, member.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("memberId").description("사용자 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );
    }

    private MockMultipartFile getMockMultipartFile() throws IOException {
        final String originalFileName = "defaultProfileImage.jfif";
        final String filePath = "src/test/resources/static/img/" + originalFileName;

        return new MockMultipartFile(
                "profileImage", //name
                originalFileName,
                "image/jpeg",
                new FileInputStream(filePath)
        );
    }
}
