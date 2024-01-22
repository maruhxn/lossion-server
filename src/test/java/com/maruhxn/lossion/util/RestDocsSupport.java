package com.maruhxn.lossion.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maruhxn.lossion.config.RestDocsTestConfiguration;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.auth.application.JwtService;
import com.maruhxn.lossion.global.auth.application.JwtUtils;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.dto.TokenDto;
import com.maruhxn.lossion.global.common.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static com.maruhxn.lossion.global.common.Constants.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@ExtendWith(RestDocumentationExtension.class)
@Import(RestDocsTestConfiguration.class)
public abstract class RestDocsSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected JwtUtils jwtUtils;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected Member member;
    protected TokenDto tokenDto;
    protected ConstraintDescriptions simpleRequestConstraints;

    @BeforeEach
    void setUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider provider
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(restDocs)
                .build();


        member = Member.builder()
                .id(1L)
                .accountId("tester")
                .username("tester")
                .password(passwordEncoder.encode("test"))
                .telNumber("01000000000")
                .email("test@test.com")
                .build();

        memberRepository.save(member);

        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
        tokenDto = jwtUtils.createJwt(jwtMemberInfo);
        jwtService.saveRefreshToken(jwtMemberInfo, tokenDto);
    }

    protected ResponseFieldsSnippet commonResponseFields(String dataName) {
        if (dataName == null) {
            return responseFields(
                    fieldWithPath("code").type(STRING).description("상태 코드"),
                    fieldWithPath("message").type(STRING).description("상태 메시지")
            );
        }

        return responseFields(
                fieldWithPath("code").type(STRING).description("상태 코드"),
                fieldWithPath("message").type(STRING).description("상태 메시지"),
                fieldWithPath("data").optional().description(dataName)
        );
    }

    protected ResponseFieldsSnippet pageResponseFields(String resultDataName) {
        return commonResponseFields("PageItem").andWithPrefix("data.",
                fieldWithPath("isFirst").type(BOOLEAN)
                        .description("첫번째 페이지 여부"),
                fieldWithPath("isLast").type(BOOLEAN)
                        .description("마지막 페이지 여부"),
                fieldWithPath("isEmpty").type(BOOLEAN)
                        .description("isEmpty"),
                fieldWithPath("totalPage").type(NUMBER)
                        .description("전체 페이지 수"),
                fieldWithPath("totalElements").type(NUMBER)
                        .description("전체 데이터 수"),
                fieldWithPath("results").type(ARRAY)
                        .description(resultDataName)
        );
    }

    protected Attributes.Attribute withPath(String path) {
        List<String> constraints = simpleRequestConstraints.descriptionsForProperty(path);
        String constraintDesc = String.join("\n\n", constraints.stream().map(s -> "- " + s).toArray(String[]::new));
        return key("constraints").value(constraintDesc);
    }

    protected MockMultipartHttpServletRequestBuilder getMockMultipartHttpServletRequestBuilder(String path, Object... id) {
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

    protected ResultActions postAction(String uri, Object req, Boolean isLogin, Object... pathParameters) throws Exception {
        MockHttpServletRequestBuilder builder = post(uri, pathParameters);

        if (req != null) {
            simpleRequestConstraints = new ConstraintDescriptions(req.getClass());

            builder
                    .content(objectMapper.writeValueAsString(req))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON);
        }

        if (isLogin) {
            builder
                    .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                    .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken());
        }

        return mockMvc.perform(builder);
    }

    protected ResultActions multipartPostAction(String uri, Class<?> reqClass, List<MockMultipartFile> images, Map<String, String> parts, Object... pathParameters) throws Exception {
        MockMultipartHttpServletRequestBuilder builder = multipart(uri, pathParameters);

        if (reqClass != null) {
            simpleRequestConstraints = new ConstraintDescriptions(reqClass);
        }

        if (images != null && !images.isEmpty()) {
            images.forEach(builder::file);
        }

        if (parts != null && !parts.isEmpty()) {
            parts.forEach((key, value) -> builder.part(new MockPart(key, value.getBytes())));
        }

        builder
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(Constants.ACCESS_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getAccessToken())
                .header(Constants.REFRESH_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getRefreshToken());

        return mockMvc.perform(builder);
    }

    protected ResultActions getAction(String uri, Boolean isLogin, Map<String, String> queryParams, Object... pathParameters) throws Exception {
        MockHttpServletRequestBuilder builder = get(uri, pathParameters);

        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach((key, value) -> builder.queryParam(key, value.toString()));
        }

        if (isLogin) {
            builder
                    .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                    .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken());
        }

        return mockMvc.perform(builder);
    }

    protected ResultActions patchAction(String uri, Object req, Boolean isLogin, Map<String, Object> queryParams, Object... pathParameters) throws Exception {
        MockHttpServletRequestBuilder builder = patch(uri, pathParameters);

        if (req != null) {
            simpleRequestConstraints = new ConstraintDescriptions(req.getClass());

            builder
                    .content(objectMapper.writeValueAsString(req))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON);
        }

        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach((key, value) -> builder.queryParam(key, value.toString()));
        }

        if (isLogin) {
            builder
                    .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                    .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken());
        }

        return mockMvc.perform(builder);
    }

    protected ResultActions multipartPatchAction(String uri, Class<?> reqClass, List<MockMultipartFile> images, Map<String, String> parts, Object... pathParameters) throws Exception {
        MockMultipartHttpServletRequestBuilder builder = getMockMultipartHttpServletRequestBuilder(uri, pathParameters);

        if (reqClass != null) {
            simpleRequestConstraints = new ConstraintDescriptions(reqClass);
        }

        if (images != null && !images.isEmpty()) {
            images.forEach(builder::file);
        }

        if (parts != null && !parts.isEmpty()) {
            parts.forEach((key, value) -> builder.part(new MockPart(key, value.getBytes())));
        }

        builder
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(Constants.ACCESS_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getAccessToken())
                .header(Constants.REFRESH_TOKEN_HEADER, Constants.BEARER_PREFIX + tokenDto.getRefreshToken());

        return mockMvc.perform(builder);
    }

    protected ResultActions deleteAction(String uri, Boolean isLogin, Object... pathParameters) throws Exception {
        MockHttpServletRequestBuilder builder = delete(uri, pathParameters);

        if (isLogin) {
            builder
                    .header(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken())
                    .header(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken());
        }

        return mockMvc.perform(builder);
    }
}
