package com.maruhxn.lossion.docs.member;

import com.maruhxn.lossion.domain.member.dto.request.UpdatePasswordReq;
import com.maruhxn.lossion.global.common.Constants;
import com.maruhxn.lossion.util.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.maruhxn.lossion.global.common.Constants.ACCESS_TOKEN_HEADER;
import static com.maruhxn.lossion.global.common.Constants.REFRESH_TOKEN_HEADER;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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

        mockMvc.perform(
                        get(MEMBER_API_PATH, member.getId())
                                .header(Constants.ACCESS_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(Constants.REFRESH_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getRefreshToken())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("프로필 조회 성공"))
                .andExpect(jsonPath("$.data.accountId").value("tester"))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.username").value("tester"))
                .andExpect(jsonPath("$.data.telNumber").value("01000000000"))
                .andExpect(jsonPath("$.data.isVerified").value(false))
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
                                                fieldWithPath("telNumber").type(STRING).description("전화번호"),
                                                fieldWithPath("isVerified").type(BOOLEAN).description("이메일 인증 여부"),
                                                fieldWithPath("profileImage").type(STRING).description("프로필 이미지")
                                        )
                        )
                );
    }

    @Test
    @DisplayName("Member 프로필 수정")
    void updateProfile() throws Exception {
        MockMultipartHttpServletRequestBuilder builder = getMockMultipartHttpServletRequestBuilder(MEMBER_API_PATH, member.getId());
        MockMultipartFile profileImage = getMockMultipartFile();

        mockMvc.perform(
                        builder
                                .file(profileImage)
                                .param("username", "username")
                                .param("email", "test!@test.com")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header(Constants.ACCESS_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(Constants.REFRESH_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getRefreshToken())
                )
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
                                        partWithName("username").optional().description("수정할 유저명"),
                                        partWithName("email").optional().description("수정할 유저명"),
                                        partWithName("profileImage").optional().description("수정할 유저명")
                                )
                        )
                );

    }

    @Test
    @DisplayName("Member 비밀번호 변경")
    void updatePassword() throws Exception {
        UpdatePasswordReq dto = UpdatePasswordReq.builder()
                .currPassword("test")
                .newPassword("updatedTest")
                .confirmNewPassword("updatedTest")
                .build();

        mockMvc.perform(
                        patch(MEMBER_API_PATH + "/password", member.getId())
                                .header(Constants.ACCESS_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(Constants.REFRESH_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getRefreshToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                                .characterEncoding(StandardCharsets.UTF_8)
                )
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
                                        fieldWithPath("currPassword").type(STRING).description("현재 비밀번호"),
                                        fieldWithPath("newPassword").type(STRING).description("새 비밀번호"),
                                        fieldWithPath("confirmNewPassword").type(STRING).description("새 비밀번호 확인")
                                )
                        )
                );
    }

    @Test
    @DisplayName("Member 회원 탈퇴")
    void withdraw() throws Exception {
        mockMvc.perform(
                        delete(MEMBER_API_PATH, member.getId())
                                .header(Constants.ACCESS_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(Constants.REFRESH_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getRefreshToken())
                )
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

    private MockMultipartHttpServletRequestBuilder getMockMultipartHttpServletRequestBuilder(String path, Object... id) {
        MockMultipartHttpServletRequestBuilder builder = RestDocumentationRequestBuilders.
                multipart(path, id);

        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod(HttpMethod.PATCH.name());
                return request;
            }
        });
        return builder;
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
