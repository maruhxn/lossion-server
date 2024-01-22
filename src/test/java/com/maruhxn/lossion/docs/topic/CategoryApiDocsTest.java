package com.maruhxn.lossion.docs.topic;

import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.member.domain.Role;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.dto.request.CreateCategoryReq;
import com.maruhxn.lossion.domain.topic.dto.request.UpdateCategoryReq;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.util.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;

import java.nio.charset.StandardCharsets;

import static com.maruhxn.lossion.global.common.Constants.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Docs] - CategoryAPIDocs")
public class CategoryApiDocsTest extends RestDocsSupport {

    private static final String CATEGORY_BASE_URL = "/api/categories";

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("카테고리 전체 조회")
    @Test
    void getAllCategories() throws Exception {
        // Given
        Category category1 = createCategory("test1");
        Category category2 = createCategory("test2");
        Category category3 = createCategory("test3");

        // When / Then
        mockMvc.perform(
                        get(CATEGORY_BASE_URL)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("카테고리 조회 성공"))
                .andExpect(jsonPath("data").isArray())
                .andExpect(jsonPath("data.size()").value(3))
                .andDo(
                        restDocs.document(
                                commonResponseFields("CategoryItem[]")
                                        .andWithPrefix("data[0].",
                                                fieldWithPath("id").type(NUMBER).description("카테고리 ID"),
                                                fieldWithPath("name").type(STRING).description("카테고리 이름"),
                                                fieldWithPath("createdAt").type(STRING).description("카테고리 생성 시각"),
                                                fieldWithPath("updatedAt").type(STRING).description("카테고리 수정 시각")
                                        )
                        )
                );

    }

    @DisplayName("카테고리 생성")
    @Test
    void createCategoryWhenIsAdmin() throws Exception {
        // Given
        Member admin = createAdmin();
        createAdminJWT(admin);

        CreateCategoryReq req = CreateCategoryReq.builder()
                .name("test")
                .build();
        simpleRequestConstraints = new ConstraintDescriptions(CreateCategoryReq.class);

        // When / Then
        mockMvc.perform(
                        post(CATEGORY_BASE_URL)
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("카테고리 생성 성공"))
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestFields(
                                        fieldWithPath("name").type(STRING).description("카테고리 이름")
                                                .attributes(withPath("name"))
                                ),
                                commonResponseFields(null)
                        )
                );


    }

    @DisplayName("어드민이 아닌 사용자의 카테고리 생성 요청 시 403 에러")
    @Test
    void createCategoryFailWhenIsNotAdmin() throws Exception {
        // Given
        CreateCategoryReq req = CreateCategoryReq.builder()
                .name("test")
                .build();

        // When / Then
        mockMvc.perform(
                        post(CATEGORY_BASE_URL)
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(ErrorCode.FORBIDDEN.name()))
                .andExpect(jsonPath("message").value(ErrorCode.FORBIDDEN.getMessage()));

    }

    @DisplayName("카테고리 생성 시 카테고리 이름이 비어있으면 400 에러")
    @Test
    void createCategoryWithoutCategoryName() throws Exception {
        // Given
        Member admin = createAdmin();
        createAdminJWT(admin);

        CreateCategoryReq req = CreateCategoryReq.builder()
                .build();

        // When / Then
        mockMvc.perform(
                        post(CATEGORY_BASE_URL)
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("카테고리명은 비어있을 수 없습니다."));

    }

    @DisplayName("카테고리 생성 시 카테고리 이름이 30자를 넘는다면 400 에러")
    @Test
    void createCategoryWithOverLengthCategoryName() throws Exception {
        // Given
        Member admin = createAdmin();
        createAdminJWT(admin);

        CreateCategoryReq req = CreateCategoryReq.builder()
                .name("asdfkhlasdkvasdlkjvakldsvaklsdhvaskldvklasdhvaklsdvhalskdvaksdhl")
                .build();

        // When / Then
        mockMvc.perform(
                        post(CATEGORY_BASE_URL)
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("카테고리명은 최대 30 글자입니다."));

    }

    @DisplayName("카테고리 업데이트")
    @Test
    void updateCategory() throws Exception {
        // Given
        Category category = createCategory("test");
        Member admin = createAdmin();
        createAdminJWT(admin);

        UpdateCategoryReq req = UpdateCategoryReq.builder()
                .name("test")
                .build();
        simpleRequestConstraints = new ConstraintDescriptions(UpdateCategoryReq.class);

        // When / Then
        mockMvc.perform(
                        patch(CATEGORY_BASE_URL + "/{categoryId}", category.getId())
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("categoryId").description("카테고리 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestFields(
                                        fieldWithPath("name").type(STRING).description("수정할 카테고리 이름")
                                                .attributes(withPath("name"))
                                )
                        )
                );

    }

    @DisplayName("어드민이 아닌 사용자의 카테고리 업데이트 요청 시 403 에러")
    @Test
    void updateCategoryFailWhenIsNotAdmin() throws Exception {
        // Given
        Category category = createCategory("test");

        UpdateCategoryReq req = UpdateCategoryReq.builder()
                .name("test!")
                .build();

        // When / Then
        mockMvc.perform(
                        patch(CATEGORY_BASE_URL + "/{categoryId}", category.getId())
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(ErrorCode.FORBIDDEN.name()))
                .andExpect(jsonPath("message").value(ErrorCode.FORBIDDEN.getMessage()));

    }

    @DisplayName("카테고리 업데이트 시 카테고리 이름이 비어있으면 400 에러")
    @Test
    void updateCategoryWithoutCategoryName() throws Exception {
        // Given
        Category category = createCategory("test");
        Member admin = createAdmin();
        createAdminJWT(admin);

        UpdateCategoryReq req = UpdateCategoryReq.builder()
                .build();

        // When / Then
        mockMvc.perform(
                        patch(CATEGORY_BASE_URL + "/{categoryId}", category.getId())
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("카테고리명은 비어있을 수 없습니다."));

    }

    @DisplayName("카테고리 수정 시 카테고리 이름이 30자를 넘는다면 400 에러")
    @Test
    void updateCategoryWithOverLengthCategoryName() throws Exception {
        // Given
        Category category = createCategory("test");
        Member admin = createAdmin();
        createAdminJWT(admin);

        UpdateCategoryReq req = UpdateCategoryReq.builder()
                .name("asdfkhlasdkvasdlkjvakldsvaklsdhvaskldvklasdhvaklsdvhalskdvaksdhl")
                .build();

        // When / Then
        mockMvc.perform(
                        patch(CATEGORY_BASE_URL + "/{categoryId}", category.getId())
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("카테고리명은 최대 30 글자입니다."));

    }

    @DisplayName("카테고리 삭제")
    @Test
    void deleteCategory() throws Exception {
        // Given
        Category category = createCategory("test");
        Member admin = createAdmin();
        createAdminJWT(admin);

        // When / Then
        mockMvc.perform(
                        delete(CATEGORY_BASE_URL + "/{categoryId}", category.getId())
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                )
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("categoryId").description("카테고리 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );

    }

    @DisplayName("어드민이 아닌 사용자의 카테고리 삭제 요청 시 403 에러")
    @Test
    void deleteCategoryFailWhenIsNotAdmin() throws Exception {
        // Given
        Category category = createCategory("test");

        // When / Then
        mockMvc.perform(
                        delete(CATEGORY_BASE_URL + "/{categoryId}", category.getId())
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(ErrorCode.FORBIDDEN.name()))
                .andExpect(jsonPath("message").value(ErrorCode.FORBIDDEN.getMessage()));

    }

    @DisplayName("카테고리 삭제 요청 시 해당 카테고리가 없으면 404 에러")
    @Test
    void deleteCategoryFailWhenNotFoundCategory() throws Exception {
        // Given
        Category category = createCategory("test");
        Member admin = createAdmin();
        createAdminJWT(admin);

        // When / Then
        mockMvc.perform(
                        delete(CATEGORY_BASE_URL + "/{categoryId}", category.getId() + 1)
                                .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                                .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(ErrorCode.NOT_FOUND_CATEGORY.name()))
                .andExpect(jsonPath("message").value(ErrorCode.NOT_FOUND_CATEGORY.getMessage()));

    }

    private void createAdminJWT(Member admin) {
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(admin);
        tokenDto = jwtUtils.createJwt(jwtMemberInfo);
        jwtService.saveRefreshToken(jwtMemberInfo, tokenDto);
    }

    private Member createAdmin() {
        Member admin = Member.builder()
                .id(1L)
                .accountId("admin")
                .username("admin")
                .password(passwordEncoder.encode("test"))
                .telNumber("01011111111")
                .email("admin@test.com")
                .build();
        admin.setRole(Role.ROLE_ADMIN);
        return memberRepository.save(admin);
    }

    private Category createCategory(String name) {
        Category category = Category.builder()
                .name(name)
                .build();

        return categoryRepository.save(category);
    }
}
