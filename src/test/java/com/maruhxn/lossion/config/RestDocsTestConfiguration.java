package com.maruhxn.lossion.config;

import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.util.CustomUserDetailsSecurityContextFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

@TestConfiguration
public class RestDocsTestConfiguration {

    @Bean
    public RestDocumentationResultHandler restDocs() {
        return MockMvcRestDocumentation.document(
                "{class_name}/{method-name}",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()), // JSON 이쁘게
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
        );
    }

    @Bean
    public CustomUserDetailsSecurityContextFactory userDetailsSecurityContextFactory(MemberRepository memberRepository) {
        return new CustomUserDetailsSecurityContextFactory(memberRepository);
    }

}
