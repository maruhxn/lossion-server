package com.maruhxn.lossion.docs.auth;

import com.maruhxn.lossion.domain.auth.dao.AuthTokenRepository;
import com.maruhxn.lossion.domain.auth.domain.AuthToken;
import com.maruhxn.lossion.domain.auth.dto.*;
import com.maruhxn.lossion.domain.member.dto.request.UpdateAnonymousPasswordReq;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.util.AesUtil;
import com.maruhxn.lossion.util.CustomWithUserDetails;
import com.maruhxn.lossion.util.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.maruhxn.lossion.global.common.Constants.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Docs] - AuthAPIDocs")
public class AuthApiDocsTest extends RestDocsSupport {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @DisplayName("비로그인 회원의 회원가입 성공 시 201을 반환한다.")
    @Test
    void signUp() throws Exception {
        // Given
        SignUpReq req = SignUpReq.builder()
                .accountId("tester2")
                .username("tester2")
                .email("test2@test.com")
                .telNumber("01000000002")
                .password("test")
                .confirmPassword("test")
                .build();
        // When / Then
        postAction("/api/auth/sign-up", req, false)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("회원가입 성공"))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("accountId").type(STRING).description("사용자 계정 ID")
                                                .attributes(withPath("accountId")),
                                        fieldWithPath("email").type(STRING).description("이메일")
                                                .attributes(withPath("email")),
                                        fieldWithPath("username").type(STRING).description("유저명")
                                                .attributes(withPath("username")),
                                        fieldWithPath("telNumber").type(STRING).description("전화번호")
                                                .attributes(withPath("telNumber")),
                                        fieldWithPath("password").type(STRING).description("비밀번호")
                                                .attributes(withPath("password")),
                                        fieldWithPath("confirmPassword").type(STRING).description("비밀번호 확인")
                                                .attributes(withPath("confirmPassword"))
                                ),
                                commonResponseFields(null)
                        )
                );
    }

    @DisplayName("로그인 회원의 회원가입 시도 시 403을 반환한다.")
    @Test
    void signUpFailWhenIsLogin() throws Exception {
        // Given
        SignUpReq req = SignUpReq.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .telNumber("01000000000")
                .password("test")
                .confirmPassword("test")
                .build();

        // When / Then
        postAction("/api/auth/sign-up", req, true)
                .andExpect(status().isForbidden());
    }

    @DisplayName("비로그인 회원의 회원가입 시도 시 올바르지 않은 요청 정보가 오면 400을 반환한다.")
    @Test
    void signUpWithInvalidRequest() throws Exception {
        // Given
        SignUpReq req = SignUpReq.builder()
                .accountId("test")
                .username("t")
                .email("test")
                .telNumber("")
                .password("t")
                .confirmPassword("")
                .build();

        // When / Then
        postAction("/api/auth/sign-up", req, false)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors.size()").value(7));
    }

    @DisplayName("비로그인 회원의 로그인 성공 시 200을 반환한다.")
    @Test
    void signIn() throws Exception {
        // Given
        LoginReq req = LoginReq.builder()
                .accountId("tester")
                .password("test")
                .build();

        // When / Then
        postAction("/api/auth/login", req, false)
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("로그인 성공"))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("accountId").type(STRING).description("사용자 계정 ID")
                                                .attributes(withPath("accountId")),
                                        fieldWithPath("password").type(STRING).description("비밀번호")
                                                .attributes(withPath("password"))
                                ),
                                commonResponseFields("TokenDto")
                                        .andWithPrefix("data.",
                                                fieldWithPath("accessToken").type(STRING).description("Access Token"),
                                                fieldWithPath("refreshToken").type(STRING).description("Refresh Token")

                                        )
                        )
                );
    }

    @DisplayName("Access Token의 refresh 성공 시 200을 반환한다.")
    @Test
    void refresh() throws Exception {
        getAction("/api/auth/refresh", true, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("Token Refresh 성공"))
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                commonResponseFields("TokenDto")
                                        .andWithPrefix("data.",
                                                fieldWithPath("accessToken").type(STRING)
                                                        .description("Access Token"),
                                                fieldWithPath("refreshToken").type(STRING)
                                                        .description("Refresh Token")
                                        )
                        )
                );

    }

    @DisplayName("refresh 시도할 때 refresh token을 넘겨주지 않은 경우 400을 반환한다.")
    @Test
    void refreshWithoutRefreshToken() throws Exception {
        mockMvc.perform(
                        get("/api/auth/refresh")
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.EMPTY_REFRESH_TOKEN.name()))
                .andExpect(jsonPath("message").value(ErrorCode.EMPTY_REFRESH_TOKEN.getMessage()));
    }

    @DisplayName("로그인 사용자의 인증 메일 발송 요청 성공 시 200을 반환한다")
    @Test
    void sendVerifyEmail() throws Exception {
        getAction("/api/auth/send-verify-email", true, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("인증 메일 발송 성공"))
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                commonResponseFields(null)
                        )
                );
    }

    @DisplayName("비로그인 사용자의 인증 메일 발송 요청 성공 시 401을 반환한다")
    @Test
    void sendVerifyEmailWhenIsNotLogin() throws Exception {
        getAction("/api/auth/send-verify-email", false, null)
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("이메일 인증 요청 성공 시 200을 반환한다")
    @Test
    void verifyEmail() throws Exception {
        // Given
        AuthToken authToken = AuthToken.builder()
                .payload("payload")
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .member(member)
                .build();
        authTokenRepository.save(authToken);

        VerifyEmailReq req = new VerifyEmailReq("payload");

        // When / Then
        postAction("/api/auth/verify-email", req, true)
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("이메일 인증 성공"))
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestFields(
                                        fieldWithPath("payload").type(STRING).description("이메일 인증 토큰 값")
                                                .attributes(withPath("payload"))
                                ),
                                commonResponseFields(null)
                        )
                );

    }

    @DisplayName("비밀번호 인증 성공 시 200을 반환한다.")
    @Test
    @CustomWithUserDetails
    void verifyPassword() throws Exception {
        // Given
        VerifyPasswordReq req = new VerifyPasswordReq("test");

        // When / Then
        postAction("/api/auth/verify-password", req, true)
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("비밀번호 인증 성공"))
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestFields(
                                        fieldWithPath("currPassword").type(STRING).description("현재 비밀번호")
                                                .attributes(withPath("currPassword"))
                                ),
                                commonResponseFields(null)
                        )
                );
    }

    @DisplayName("익명 사용자의 경우, 아이디와 이메일을 통해 인증 메일 발송을 요청할 수 있다. 성공 시 200을 반환한다.")
    @Test
    void sendEmailWithAnonymous() throws Exception {
        // Given
        SendAnonymousEmailReq req = SendAnonymousEmailReq.builder()
                .accountId("tester")
                .email("test@test.com")
                .build();

        // When / Then
        postAction("/api/auth/anonymous/send-verify-email", req, false)
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("인증 메일 발송 성공"))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("accountId").type(STRING).description("인증 시도할 사용자의 계정 ID")
                                                .attributes(withPath("accountId")),
                                        fieldWithPath("email").type(STRING).description("인증 시도할 사용자의 email")
                                                .attributes(withPath("email"))
                                ),
                                commonResponseFields(null)
                        )
                );
    }

    @DisplayName("익명 사용자의 경우, 발송된 인증 메일의 payload를 함께 전달하면 인증키를 발급받을 수 있으며, 성공 시 200을 반환한다.")
    @Test
    void getAuthKeyToFindPassword() throws Exception {
        // Given
        ReflectionTestUtils.setField(AesUtil.class, "privateKey", "verySecretKey");
        ReflectionTestUtils.setField(AesUtil.class, "privateIv", "1234123412341234");

        AuthToken authToken = AuthToken.builder()
                .payload("payload")
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .member(member)
                .build();
        authTokenRepository.save(authToken);

        GetTokenReq req = GetTokenReq.builder()
                .accountId("tester")
                .email("test@test.com")
                .payload("payload")
                .build();

        String authKey = AesUtil.encrypt(req.getPayload());

        // When / Then
        postAction("/api/auth/anonymous/get-token", req, false)
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("유저 정보 조회 성공"))
                .andExpect(jsonPath("data").value(authKey))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("accountId").type(STRING).description("인증 시도할 사용자의 계정 ID")
                                                .attributes(withPath("accountId")),
                                        fieldWithPath("email").type(STRING).description("인증 시도할 사용자의 email")
                                                .attributes(withPath("email")),
                                        fieldWithPath("payload").type(STRING).description("발급받은 이메일 인증 토큰 값")
                                                .attributes(withPath("payload"))
                                ),
                                commonResponseFields(null)
                                        .and(
                                                fieldWithPath("data").type(STRING).description("인증키")
                                        )
                        )
                );
    }

    @DisplayName("익명 사용자의 경우, 발급받은 인증키와 함께 비밀번호 변경 정보를 넘겨 비밀번호를 변경할 수 있으며, 성공 시 204를 반환한다.")
    @Test
    void updateAnonymousPassword() throws Exception {
        // Given
        ReflectionTestUtils.setField(AesUtil.class, "privateKey", "verySecretKey");
        ReflectionTestUtils.setField(AesUtil.class, "privateIv", "1234123412341234");

        AuthToken authToken = AuthToken.builder()
                .payload("payload")
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .member(member)
                .build();
        authTokenRepository.save(authToken);

        String authKey = AesUtil.encrypt("payload");

        UpdateAnonymousPasswordReq req = UpdateAnonymousPasswordReq.builder()
                .newPassword("test")
                .confirmNewPassword("test")
                .build();

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("authKey", authKey);

        // When / Then
        patchAction("/api/auth/anonymous/password", req, false, queryParams)
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("authKey").description("발급받은 인증 키")
                                ),
                                requestFields(
                                        fieldWithPath("newPassword").type(STRING).description("새로운 비밀번호")
                                                .attributes(withPath("newPassword")),
                                        fieldWithPath("confirmNewPassword").type(STRING).description("새로운 비밀번호 확인")
                                                .attributes(withPath("confirmNewPassword"))
                                )
                        )
                );

    }

    @DisplayName("로그인한 사용자가 로그아웃 성공 시 204를 반환한다.")
    @Test
    void logout() throws Exception {
        patchAction("/api/auth/logout", null, true, null)
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );

    }

}
