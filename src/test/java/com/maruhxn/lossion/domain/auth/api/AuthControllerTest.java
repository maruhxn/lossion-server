package com.maruhxn.lossion.domain.auth.api;

import com.maruhxn.lossion.domain.auth.dto.*;
import com.maruhxn.lossion.domain.member.dto.request.UpdateAnonymousPasswordReq;
import com.maruhxn.lossion.global.common.Constants;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.util.ControllerTestSupport;
import com.maruhxn.lossion.util.CustomWithUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Controller] - AuthController")
class AuthControllerTest extends ControllerTestSupport {

    @DisplayName("회원가입 성공 시 201을 반환한다.")
    @Test
    @WithMockUser
    void signUp() throws Exception {
        // Given
        SignUpReq req = SignUpReq.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password("test")
                .confirmPassword("test")
                .build();

        // When / Then
        mockMvc.perform(
                        post("/api/auth/sign-up")
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("회원가입 성공"));
    }

    @DisplayName("회원가입 시도 시 올바르지 않은 요청 정보가 오면 400을 반환한다.")
    @Test
    @WithMockUser
    void signUpWithInvalidRequest() throws Exception {
        // Given
        SignUpReq req = SignUpReq.builder()
                .accountId("test")
                .username("t")
                .email("test")
                .password("t")
                .confirmPassword("")
                .build();

        // When / Then
        mockMvc.perform(
                        post("/api/auth/sign-up")
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors.size()").value(5));
    }

    @DisplayName("Access Token의 refresh 성공 시 200을 반환한다.")
    @Test
    @WithMockUser
    void refresh() throws Exception {

        // When / Then
        mockMvc.perform(
                        get("/api/auth/refresh")
                                .header(Constants.REFRESH_TOKEN_HEADER, "bearerRefreshToken")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("Token Refresh 성공"));
    }

    @DisplayName("refresh 시도할 때 refresh token을 넘겨주지 않은 경우 400을 반환한다.")
    @Test
    @WithMockUser
    void refreshWithoutRefreshToken() throws Exception {

        // When / Then
        mockMvc.perform(
                        get("/api/auth/refresh")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.EMPTY_REFRESH_TOKEN.name()))
                .andExpect(jsonPath("message").value(ErrorCode.EMPTY_REFRESH_TOKEN.getMessage()));
    }

    @DisplayName("인증 메일 발송 요청 성공 시 200을 반환한다")
    @Test
    @CustomWithUserDetails
    void sendVerifyEmail() throws Exception {
        // When / Then
        mockMvc.perform(
                        get("/api/auth/send-verify-email")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("인증 메일 발송 성공"));
    }

    @DisplayName("이메일 인증 요청 성공 시 200을 반환한다")
    @Test
    @CustomWithUserDetails
    void verifyEmail() throws Exception {
        // Given
        VerifyEmailReq req = new VerifyEmailReq("payload");

        // When / Then
        mockMvc.perform(
                        post("/api/auth/verify-email")
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("이메일 인증 성공"));
    }

    @DisplayName("비밀번호 인증 성공 시 200을 반환한다.")
    @Test
    @CustomWithUserDetails
    void verifyPassword() throws Exception {
        // Given
        VerifyPasswordReq req = new VerifyPasswordReq("test");

        // When / Then
        mockMvc.perform(
                        post("/api/auth/verify-password")
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("비밀번호 인증 성공"));
    }

    @DisplayName("익명 사용자의 경우, 아이디와 이메일을 통해 인증 메일 발송을 요청할 수 있다. 성공 시 200을 반환한다.")
    @Test
    @CustomWithUserDetails
    void sendEmailWithAnonymous() throws Exception {
        // Given
        SendAnonymousEmailReq req = SendAnonymousEmailReq.builder()
                .accountId("tester")
                .email("test@test.com")
                .build();

        // When / Then
        mockMvc.perform(
                        post("/api/auth/anonymous/send-verify-email")
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("인증 메일 발송 성공"));
    }

    @DisplayName("익명 사용자의 경우, 발송된 인증 메일의 payload를 함께 전달하면 인증키를 발급받을 수 있으며, 성공 시 200을 반환한다.")
    @Test
    @CustomWithUserDetails
    void getAuthKeyToFindPassword() throws Exception {
        // Given
        GetTokenReq req = GetTokenReq.builder()
                .accountId("tester")
                .email("test@test.com")
                .payload("payload")
                .build();

        given(authService.getAuthKeyToFindPassword(any(GetTokenReq.class), any(LocalDateTime.class)))
                .willReturn("auth-key");

        // When / Then
        mockMvc.perform(
                        post("/api/auth/anonymous/get-token")
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("유저 정보 조회 성공"))
                .andExpect(jsonPath("data").value("auth-key"));
    }

    @DisplayName("익명 사용자의 인증키 발급 시도 시 payload를 넘겨주지 않으면, 400을 반환한다.")
    @Test
    @WithMockUser
    void getAuthKeyToFindPasswordWithoutPayload() throws Exception {
        // Given
        GetTokenReq req = GetTokenReq.builder()
                .accountId("tester")
                .email("test@test.com")
                .build();

        // When / Then
        mockMvc.perform(
                        post("/api/auth/anonymous/get-token")
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors[0].reason").value("토큰을 입력해주세요."));
    }

    @DisplayName("익명 사용자의 경우, 발급받은 인증키와 함께 비밀번호 변경 정보를 넘겨 비밀번호를 변경할 수 있으며, 성공 시 204를 반환한다.")
    @Test
    @WithMockUser
    void updateAnonymousPassword() throws Exception {
        // Given
        String authKey = "auth-key";
        UpdateAnonymousPasswordReq req = UpdateAnonymousPasswordReq.builder()
                .newPassword("test")
                .confirmNewPassword("test")
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/auth/anonymous/password")
                                .queryParam("authKey", authKey)
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("익명 사용자의 비밀번호를 변경 시도 시 authKey를 넘겨주지 않으면 400을 반환한다.")
    @Test
    @WithMockUser
    void updateAnonymousPasswordWithoutAuthKey() throws Exception {
        // Given
        String authKey = "auth-key";
        UpdateAnonymousPasswordReq req = UpdateAnonymousPasswordReq.builder()
                .newPassword("test")
                .confirmNewPassword("test")
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/auth/anonymous/password")
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.PATH_VAR_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.PATH_VAR_ERROR.getMessage()));
    }

    @DisplayName("익명 사용자의 비밀번호를 변경 시도 시 20글자를 초과하는 비밀번호라면, 400을 반환한다.")
    @Test
    @WithMockUser
    void updateAnonymousPasswordWithOverLengthPassword() throws Exception {
        // Given
        String authKey = "auth-key";
        UpdateAnonymousPasswordReq req = UpdateAnonymousPasswordReq.builder()
                .newPassword("testtesttesttesttest!")
                .confirmNewPassword("testtesttesttesttest!")
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/auth/anonymous/password")
                                .queryParam("authKey", authKey)
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors[0].reason").value("비밀번호는 2 ~ 20 글자입니다."));
    }

    @DisplayName("로그인한 사용자가 로그아웃 성공 시 204를 반환한다.")
    @Test
    @WithMockUser
    void logout() throws Exception {
        // When / Then
        mockMvc.perform(
                        patch("/api/auth/logout")
                                .header(Constants.REFRESH_TOKEN_HEADER, "bearerRefreshToken")
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("로그아웃 시도 시 refresh token을 넘겨주지 않으면 400을 반환한다.")
    @Test
    @WithMockUser
    void logoutWithoutRefreshToken() throws Exception {
        // When / Then
        mockMvc.perform(
                        patch("/api/auth/logout")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.EMPTY_REFRESH_TOKEN.name()))
                .andExpect(jsonPath("message").value(ErrorCode.EMPTY_REFRESH_TOKEN.getMessage()));
    }
}