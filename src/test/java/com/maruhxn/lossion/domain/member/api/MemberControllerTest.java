package com.maruhxn.lossion.domain.member.api;

import com.maruhxn.lossion.domain.member.dto.request.UpdatePasswordReq;
import com.maruhxn.lossion.util.CustomWithUserDetails;
import com.maruhxn.lossion.util.TestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.InputStream;

import static com.maruhxn.lossion.global.common.Constants.BASIC_PROFILE_IMAGE_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("컨트롤러 - MemberController")
class MemberControllerTest extends TestSupport {

    final String MEMBER_API_PATH = "/api/members/{memberId}";

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 프로필 조회 테스트")
    void shouldGetProfileWhenIsOwner() throws Exception {
        mvc.perform(
                        get(MEMBER_API_PATH, member.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("프로필 조회 성공"))
                .andExpect(jsonPath("$.data.accountId").value("tester"))
                .andExpect(jsonPath("$.data.username").value("tester"))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.telNumber").value("01012345678"))
                .andExpect(jsonPath("$.data.profileImage").value(BASIC_PROFILE_IMAGE_NAME));
    }

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 프로필 업데이트 테스트")
    void shouldUpdateProfileWhenIsOwner() throws Exception {
        MockMultipartHttpServletRequestBuilder builder = getMockMultipartHttpServletRequestBuilder();
//        ConstraintDescriptions simpleRequestConstraints = new ConstraintDescriptions(UpdateMemberProfileReq.class);
        final String originalFileName = BASIC_PROFILE_IMAGE_NAME; //파일명
        String filePath = "src/test/resources/static/img/" + originalFileName;
        FileSystemResource resource = new FileSystemResource(filePath);
        InputStream inputStream = resource.getInputStream();
        MockMultipartFile image1 = new MockMultipartFile("profileImage", originalFileName, "image/jpeg", inputStream);

        mvc.perform(
                        builder
                                .part(new MockPart("username", "tester!".getBytes()))
                                .file(image1)
                )
                .andExpect(status().isNoContent());
    }


    /**
     * multipart 테스트에서는 patch http method를 사용할 수 없기에 따로 builder를 통해 생성
     *
     * @return
     */
    private MockMultipartHttpServletRequestBuilder getMockMultipartHttpServletRequestBuilder() {
        MockMultipartHttpServletRequestBuilder builder = RestDocumentationRequestBuilders.
                multipart(MEMBER_API_PATH, member.getId());

        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod(HttpMethod.PATCH.name());
                return request;
            }
        });
        return builder;
    }

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 비밀번호 변경 테스트")
    void shouldUpdatePasswordWhenIsOwner() throws Exception {
        UpdatePasswordReq dto = UpdatePasswordReq.builder()
                .currPassword("test")
                .newPassword("updatedTest")
                .confirmNewPassword("updatedTest")
                .build();

        mvc.perform(
                patch(MEMBER_API_PATH + "/update-password", member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isNoContent());
    }

    @Test
    @CustomWithUserDetails
    @DisplayName("Member 회원 탈퇴 테스트")
    void shouldWithdrawWhenIsOwner() throws Exception {
        mvc.perform(
                delete(MEMBER_API_PATH, member.getId())
        ).andExpect(status().isNoContent());
    }
}