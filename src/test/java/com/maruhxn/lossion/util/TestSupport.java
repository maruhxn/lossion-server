package com.maruhxn.lossion.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maruhxn.lossion.config.MvcTestConfiguration;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.member.domain.Role;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(MvcTestConfiguration.class)
public class TestSupport {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected Member member;
    protected Member admin;

    protected Category category;

    protected Topic topic;

    @BeforeEach
    void setUp(
            final WebApplicationContext context
    ) {
        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        member = Member.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password(passwordEncoder.encode("test"))
                .telNumber("01012345678")
                .build();

        admin = Member.builder()
                .accountId("admin")
                .username("admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin"))
                .telNumber("01000000000")
                .build();
        admin.setRole(Role.ROLE_ADMIN);

        memberRepository.saveAll(List.of(member, admin));

        category = Category.builder()
                .name("test")
                .build();

        categoryRepository.save(category);

        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);

        topic = Topic.builder()
                .title("test")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(member)
                .category(category)
                .build();

        topicRepository.save(topic);


    }
}
