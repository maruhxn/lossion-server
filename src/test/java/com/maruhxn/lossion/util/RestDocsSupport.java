package com.maruhxn.lossion.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maruhxn.lossion.config.RestDocsTestConfiguration;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.auth.application.JwtService;
import com.maruhxn.lossion.global.auth.application.JwtUtils;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.dto.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.restdocs.payload.JsonFieldType.STRING;
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
    private JwtUtils jwtUtils;

    @Autowired
    private JwtService jwtService;

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

    public ResponseFieldsSnippet commonResponseFields(String dataName) {
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

    public Attributes.Attribute withPath(String path) {
        List<String> constraints = simpleRequestConstraints.descriptionsForProperty(path);
        String constraintDesc = String.join("\n\n", constraints.stream().map(s -> "- " + s).toArray(String[]::new));
        return key("constraints").value(constraintDesc);
    }
}
