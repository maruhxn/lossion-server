package com.maruhxn.lossion.domain.member.api;

import com.maruhxn.lossion.domain.member.dto.request.UpdatePasswordReq;
import com.maruhxn.lossion.domain.member.dto.response.ProfileItem;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.util.ControllerTestSupport;
import com.maruhxn.lossion.util.CustomWithUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[컨트롤러] - MemberController")
class MemberControllerTest extends ControllerTestSupport {

    final String MEMBER_API_PATH = "/api/members/{memberId}";

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 프로필 조회")
    void shouldGetProfileWhenIsOwner() throws Exception {
        given(memberService.getProfile(anyLong()))
                .willReturn(ProfileItem.builder().build());

        mockMvc.perform(
                        get(MEMBER_API_PATH, 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("프로필 조회 성공"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 프로필 수정")
    void updateProfile() throws Exception {
        MockMultipartFile image1 = getMockMultipartFile();

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, MEMBER_API_PATH, 1)
                                .file(image1)
                                .param("username", "username")
                                .param("email", "test@test.com")
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

    }

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 프로필 수정 시 1글자 이름은 400 에러를 반환한다.")
    void updateProfileWithShortLengthUsername() throws Exception {
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, MEMBER_API_PATH, 1)
                                .param("username", "1")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("유저명은 2 ~ 10 글자입니다."));

    }

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 프로필 수정 시 10글자 초과의 이름은 400 에러를 반환한다.")
    void updateProfileWithOverLengthUsername() throws Exception {
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, MEMBER_API_PATH, 1)
                                .param("username", "overLength!")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("유저명은 2 ~ 10 글자입니다."));

    }

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 프로필 수정 시 이메일 형식에 맞지 않는 경우, 400 에러를 반환한다.")
    void updateProfileWithInvalidEmail() throws Exception {
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, MEMBER_API_PATH, 1)
                                .param("email", "email")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("이메일 형식에 맞추어 입력해주세요."));

    }

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 비밀번호 변경")
    void updatePassword() throws Exception {
        UpdatePasswordReq dto = UpdatePasswordReq.builder()
                .currPassword("test")
                .newPassword("updatedTest")
                .confirmNewPassword("updatedTest")
                .build();

        mockMvc.perform(
                patch(MEMBER_API_PATH + "/password", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(csrf())
        ).andExpect(status().isNoContent());
    }

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 비밀번호 변경 시 비밀번호 길이가 20글자를 초과할 경우, 400 에러를 반환한다.")
    void updatePasswordWithOverLength() throws Exception {
        UpdatePasswordReq dto = UpdatePasswordReq.builder()
                .currPassword("test")
                .newPassword("updatedTestButOver!!!!")
                .confirmNewPassword("updatedTest")
                .build();

        mockMvc.perform(
                        patch(MEMBER_API_PATH + "/password", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("비밀번호는 2 ~ 20 글자입니다."));
    }

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 회원 탈퇴")
    void withdraw() throws Exception {
        mockMvc.perform(
                delete(MEMBER_API_PATH, 1)
                        .with(csrf())
        ).andExpect(status().isNoContent());
    }

    private MockMultipartFile getMockMultipartFile() throws IOException {
        final String fileName = "defaultProfileImage"; // 파일명
        final String contentType = "jfif"; // 파일타입
        final String filePath = "src/test/resources/static/img/" + fileName + "." + contentType; //파일경로

        return new MockMultipartFile(
                "profileImage", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                new FileInputStream(filePath)
        );
    }
}