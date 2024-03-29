package com.maruhxn.lossion.docs.topic;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicImageRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.domain.topic.domain.TopicImage;
import com.maruhxn.lossion.domain.topic.domain.VoteType;
import com.maruhxn.lossion.domain.topic.dto.request.CreateTopicReq;
import com.maruhxn.lossion.domain.topic.dto.request.UpdateTopicReq;
import com.maruhxn.lossion.domain.topic.dto.request.VoteRequest;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.util.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maruhxn.lossion.global.common.Constants.ACCESS_TOKEN_HEADER;
import static com.maruhxn.lossion.global.common.Constants.REFRESH_TOKEN_HEADER;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Docs] - TopicAPIDocs")
public class TopicApiDocsTest extends RestDocsSupport {

    private static final String TOPIC_BASE_URL = "/api/topics";

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicImageRepository topicImageRepository;

    @DisplayName("주제 리스트를 페이징 조회한다.")
    @Test
    void getTopicsByQuery() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        createTopic("test1", closedAt, now, member, category);
        createTopic("test2", closedAt, now, member, category);
        createTopic("test3", closedAt, now, member, category);

        // When / Then
        getAction(TOPIC_BASE_URL, false, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("주제 리스트 조회 성공"))
                .andExpect(jsonPath("data.results.size()").value(3))
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("size").optional().description("조회 결과 크기"),
                                        parameterWithName("page").optional().description("페이지"),
                                        parameterWithName("title").optional().description("제목 검색"),
                                        parameterWithName("content").optional().description("내용 검색"),
                                        parameterWithName("author").optional().description("작성자 이름 검색")
                                ),
                                pageResponseFields("TopicItem[]")
                                        .andWithPrefix("data.results[0].",
                                                fieldWithPath("topicId").type(NUMBER)
                                                        .description("주제 ID"),
                                                fieldWithPath("title").type(STRING)
                                                        .description("주제 제목"),
                                                fieldWithPath("viewCount").type(NUMBER)
                                                        .description("조회 수"),
                                                fieldWithPath("commentCount").type(NUMBER)
                                                        .description("댓글 수"),
                                                fieldWithPath("favoriteCount").type(NUMBER)
                                                        .description("좋아요 수"),
                                                fieldWithPath("voteCount").type(NUMBER)
                                                        .description("투표 수"),
                                                fieldWithPath("closedAt").type(STRING)
                                                        .description("토론 종료 시간"),
                                                fieldWithPath("createdAt").type(STRING)
                                                        .description("토론 생성 시간"),
                                                fieldWithPath("isClosed").type(BOOLEAN)
                                                        .description("토론 종료 여부"),
                                                fieldWithPath("categoryItem.id").type(NUMBER)
                                                        .description("카테고리 ID"),
                                                fieldWithPath("categoryItem.name").type(STRING)
                                                        .description("카테고리 이름"),
                                                fieldWithPath("categoryItem.createdAt").type(STRING)
                                                        .description("카테고리 생성 시각"),
                                                fieldWithPath("categoryItem.updatedAt").type(STRING)
                                                        .description("카테고리 수정 시각"),
                                                fieldWithPath("author.authorId").type(NUMBER)
                                                        .description("작성자 ID"),
                                                fieldWithPath("author.username").type(STRING)
                                                        .description("작성자 유저명"),
                                                fieldWithPath("author.profileImage").type(STRING)
                                                        .description("작성자 프로필 이미지")
                                        )
                        )
                );
    }

    @DisplayName("주제 리스트를 조회 시 '작성자' 조건은 10글자를 넘길 수 없다.")
    @Test
    void getTopicsByQueryWithOverLengthAuthorName() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        createTopic("test1", closedAt, now, member, category);
        createTopic("test2", closedAt, now, member, category);
        createTopic("test3", closedAt, now, member, category);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("author", "anyLongName");

        // When / Then
        getAction(TOPIC_BASE_URL, false, queryParams)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("유저명 검색은 최대 10글자입니다."));
    }

    @Test
    @DisplayName("주제를 생성한다.")
    void createTopic() throws Exception {
        // Given
        Category category = createCategory();
        String closedAtStr = LocalDateTime.now().plusDays(1).toString();

        Map<String, String> parts = new HashMap<>();
        parts.put("title", "test");
        parts.put("description", "test");
        parts.put("firstChoice", "firstChoice");
        parts.put("secondChoice", "secondChoice");
        parts.put("closedAt", closedAtStr);
        parts.put("categoryId", category.getId().toString());

        // When / Then
        multipartPostAction(TOPIC_BASE_URL, CreateTopicReq.class, List.of(), parts)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("주제 생성 성공"));

    }

    @Test
    @DisplayName("이미지와 함께 주제를 생성한다.")
    void createTopicWithImages() throws Exception {
        // Given
        Category category = createCategory();
        String closedAtStr = LocalDateTime.now().plusDays(1).toString();
        MockMultipartFile image1 = getMockMultipartFile();
        MockMultipartFile image2 = getMockMultipartFile();

        Map<String, String> parts = new HashMap<>();
        parts.put("title", "test");
        parts.put("description", "test");
        parts.put("firstChoice", "firstChoice");
        parts.put("secondChoice", "secondChoice");
        parts.put("closedAt", closedAtStr);
        parts.put("categoryId", category.getId().toString());

        // When / Then
        multipartPostAction(TOPIC_BASE_URL, CreateTopicReq.class, List.of(image1, image2), parts)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("주제 생성 성공"))
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestParts(
                                        partWithName("title").description("토론 주제 제목")
                                                .attributes(withPath("title")),
                                        partWithName("description").description("토론 주제 내용")
                                                .attributes(withPath("description")),
                                        partWithName("firstChoice").description("토론 주제 1번 선택지")
                                                .attributes(withPath("firstChoice")),
                                        partWithName("secondChoice").description("토론 주제 2번 선택지")
                                                .attributes(withPath("secondChoice")),
                                        partWithName("closedAt").description("토론 종료 시각")
                                                .attributes(withPath("closedAt")),
                                        partWithName("categoryId").description("카테고리")
                                                .attributes(withPath("categoryId")),
                                        partWithName("images").description("포함 이미지 리스트")
                                                .attributes(withPath("images"))
                                ),
                                commonResponseFields(null)
                        )
                );
    }

    @Test
    @DisplayName("주제를 생성 시 제목을 전달하지 않으면 400 에러를 반환한다.")
    void createTopicWithoutTitle() throws Exception {
        // Given
        Category category = createCategory();
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();

        Map<String, String> parts = new HashMap<>();
        parts.put("description", "test");
        parts.put("firstChoice", "firstChoice");
        parts.put("secondChoice", "secondChoice");
        parts.put("closedAt", closedAtStr);
        parts.put("categoryId", category.getId().toString());

        // When / Then
        multipartPostAction(TOPIC_BASE_URL, CreateTopicReq.class, List.of(), parts)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("제목을 입력해주세요."));

    }

    @Test
    @DisplayName("주제를 생성 시 1글자 제목을 전달하면 400 에러를 반환한다.")
    void createTopicWithShortTitle() throws Exception {
        // Given
        Category category = createCategory();
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();

        Map<String, String> parts = new HashMap<>();
        parts.put("title", "1");
        parts.put("description", "test");
        parts.put("firstChoice", "firstChoice");
        parts.put("secondChoice", "secondChoice");
        parts.put("closedAt", closedAtStr);
        parts.put("categoryId", category.getId().toString());

        // When / Then
        multipartPostAction(TOPIC_BASE_URL, CreateTopicReq.class, List.of(), parts)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("제목은 2 ~ 255 글자입니다."));
    }

    @Test
    @DisplayName("주제를 생성 시 내용을 전달하지 않으면 400 에러를 반환한다.")
    void createTopicWithoutDescription() throws Exception {
        // Given
        Category category = createCategory();
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();

        Map<String, String> parts = new HashMap<>();
        parts.put("title", "test");
        parts.put("firstChoice", "firstChoice");
        parts.put("secondChoice", "secondChoice");
        parts.put("closedAt", closedAtStr);
        parts.put("categoryId", category.getId().toString());

        // When / Then
        multipartPostAction(TOPIC_BASE_URL, CreateTopicReq.class, List.of(), parts)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("내용을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("주제를 생성 시 선택지를 전달하지 않으면 400 에러를 반환한다.")
    void createTopicWithoutChoice() throws Exception {
        // Given
        Category category = createCategory();
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();

        Map<String, String> parts = new HashMap<>();
        parts.put("title", "test");
        parts.put("description", "test");
        parts.put("closedAt", closedAtStr);
        parts.put("categoryId", category.getId().toString());

        // When / Then
        multipartPostAction(TOPIC_BASE_URL, CreateTopicReq.class, List.of(), parts)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors.size()").value(2));
    }

    @Test
    @DisplayName("주제를 생성 시 토론 종료 시각을 전달하지 않으면 400 에러를 반환한다.")
    void createTopicWithoutClosedAt() throws Exception {
        // Given
        Category category = createCategory();

        Map<String, String> parts = new HashMap<>();
        parts.put("title", "test");
        parts.put("description", "test");
        parts.put("firstChoice", "firstChoice");
        parts.put("secondChoice", "secondChoice");
        parts.put("categoryId", category.getId().toString());

        // When / Then
        multipartPostAction(TOPIC_BASE_URL, CreateTopicReq.class, List.of(), parts)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("토론 종료 시각을 입력해주세요."));
    }

    @Test
    @DisplayName("주제를 생성 시 카테고리 아이디를 전달하지 않으면 400 에러를 반환한다.")
    void createTopicWithoutCategoryId() throws Exception {
        // Given
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();

        Map<String, String> parts = new HashMap<>();
        parts.put("title", "test");
        parts.put("description", "test");
        parts.put("firstChoice", "firstChoice");
        parts.put("secondChoice", "secondChoice");
        parts.put("closedAt", closedAtStr);

        // When / Then
        multipartPostAction(TOPIC_BASE_URL, CreateTopicReq.class, List.of(), parts)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("카테고리를 선택해주세요."));
    }

    @DisplayName("주제를 상세 조회할 수 있다.")
    @Test
    void getTopicDetail() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test1", closedAt, now, member, category);

        // When / Then
        getAction(TOPIC_BASE_URL + "/{topicId}", false, null, topic.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("주제 조회 성공"))
                .andExpect(jsonPath("data").isNotEmpty())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("토론 주제 아이디")
                                ),
                                commonResponseFields("TopicDetailItem")
                                        .andWithPrefix("data.",
                                                fieldWithPath("topicId").type(NUMBER).description("topicId"),
                                                fieldWithPath("categoryItem.id").type(NUMBER).description("카테고리 아이디"),
                                                fieldWithPath("categoryItem.name").type(STRING).description("카테고리명"),
                                                fieldWithPath("categoryItem.createdAt").type(STRING).description("카테고리 생성일"),
                                                fieldWithPath("categoryItem.updatedAt").type(STRING).description("카테고리 수정일"),
                                                fieldWithPath("title").type(STRING).description("제목"),
                                                fieldWithPath("description").type(STRING).description("내용"),
                                                fieldWithPath("firstChoice").type(STRING).description("1번 선택지"),
                                                fieldWithPath("secondChoice").type(STRING).description("2번 선택지"),
                                                fieldWithPath("author.authorId").type(NUMBER).description("작성자 아이디"),
                                                fieldWithPath("author.username").type(STRING).description("작성자 유저명"),
                                                fieldWithPath("author.profileImage").type(STRING).description("작성자 프로필 이미지"),
                                                fieldWithPath("viewCount").type(NUMBER).description("조회 수"),
                                                fieldWithPath("commentCount").type(NUMBER).description("댓글 수"),
                                                fieldWithPath("favoriteCount").type(NUMBER).description("좋아요 수"),
                                                fieldWithPath("voteCountInfo.voteCount").type(NUMBER).description("총 투표 수"),
                                                fieldWithPath("voteCountInfo.firstChoiceCount").type(NUMBER).description("1번 선택지 투표 수"),
                                                fieldWithPath("voteCountInfo.secondChoiceCount").type(NUMBER).description("2번 선택지 투표 수"),
                                                fieldWithPath("isClosed").type(BOOLEAN).description("토론 종료 여부"),
                                                fieldWithPath("createdAt").type(STRING).description("토론 주제 생성일"),
                                                fieldWithPath("updatedAt").type(STRING).description("토론 주제 수정일"),
                                                fieldWithPath("closedAt").type(STRING).description("토론 종료일"),
                                                fieldWithPath("images").type(ARRAY).description("이미지 리스트")
                                        )
                        )
                );
    }

    @DisplayName("주제를 상세 조회 시 올바르지 않은 topicId를 전달할 경우 400 에러를 반환한다.")
    @Test
    void getTopicDetailWithInvalidPathVariable() throws Exception {
        getAction(TOPIC_BASE_URL + "/{topicId}", false, null, "hack")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.PATH_VAR_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.PATH_VAR_ERROR.getMessage()));
    }

    @DisplayName("토론 주제를 수정할 수 있다.")
    @Test
    void updateTopic() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, member, category);

        MockMultipartFile image1 = getMockMultipartFile();

        Map<String, String> parts = new HashMap<>();
        parts.put("title", "test");
        parts.put("description", "test");
        parts.put("firstChoice", "firstChoice");
        parts.put("secondChoice", "secondChoice");
        parts.put("closedAt", closedAt.toString());
        parts.put("categoryId", category.getId().toString());

        // When / Then
        multipartPatchAction(TOPIC_BASE_URL + "/{topicId}", UpdateTopicReq.class, List.of(image1), true, parts, topic.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("토론 주제 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestParts(
                                        partWithName("title").optional().description("토론 주제 제목")
                                                .attributes(withPath("title")),
                                        partWithName("description").optional().description("토론 주제 내용")
                                                .attributes(withPath("description")),
                                        partWithName("firstChoice").optional().description("토론 주제 1번 선택지")
                                                .attributes(withPath("firstChoice")),
                                        partWithName("secondChoice").optional().description("토론 주제 2번 선택지")
                                                .attributes(withPath("secondChoice")),
                                        partWithName("closedAt").optional().description("토론 종료 시각")
                                                .attributes(withPath("closedAt")),
                                        partWithName("categoryId").optional().description("카테고리")
                                                .attributes(withPath("categoryId")),
                                        partWithName("images").optional().description("포함 이미지 리스트")
                                                .attributes(withPath("images"))
                                )
                        )
                );
    }

    @DisplayName("작성자가 아닌 경우 토론 수정 요청 시 403 에러를 반환한다.")
    @Test
    void updateTopicFailWhenIsNotAuthor() throws Exception {
        // Given
        Member subMember = Member.builder()
                .accountId("tester2")
                .username("tester2")
                .password(passwordEncoder.encode("test2"))
                .email("test2@test.com")
                .build();
        memberRepository.save(subMember);

        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, subMember, category);

        MockMultipartFile image1 = getMockMultipartFile();

        Map<String, String> parts = new HashMap<>();
        parts.put("title", "test");
        parts.put("description", "test");
        parts.put("firstChoice", "firstChoice");
        parts.put("secondChoice", "secondChoice");
        parts.put("closedAt", closedAt.toString());
        parts.put("categoryId", category.getId().toString());

        // When / Then
        multipartPatchAction(TOPIC_BASE_URL + "/{topicId}", UpdateTopicReq.class, List.of(image1), true, parts, topic.getId())
                .andExpect(status().isForbidden());
    }

    @DisplayName("토론을 종료할 수 있다.")
    @Test
    void closeTopic() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, member, category);

        // When / Then
        patchAction(TOPIC_BASE_URL + "/{topicId}/status", null, true, null, topic.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("토론 주제 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );
    }

    @DisplayName("토론 종료 시 올바르지 않은 topicId를 전달할 경우 400 에러를 반환한다.")
    @Test
    void closeTopicWithInvalidPathVariable() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, member, category);

        // When / Then
        patchAction(TOPIC_BASE_URL + "/{topicId}/status", null, true, null, "hack")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.PATH_VAR_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.PATH_VAR_ERROR.getMessage()));
    }

    @DisplayName("주제를 삭제할 수 있다.")
    @Test
    void deleteTopic() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, member, category);

        // When / Then
        deleteAction(TOPIC_BASE_URL + "/{topicId}", true, topic.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("토론 주제 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );
    }

    @DisplayName("주제 삭제 시 올바르지 않은 topicId를 전달할 경우 400 에러를 반환한다.")
    @Test
    void deleteTopicWithInvalidPathVariable() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, member, category);

        // When / Then
        deleteAction(TOPIC_BASE_URL + "/{topicId}", true, "hack")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.PATH_VAR_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.PATH_VAR_ERROR.getMessage()));
    }

    @DisplayName("이미지를 삭제할 수 있다.")
    @Test
    void deleteTopicImage() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, member, category);
        String storedName = fileService.storeOneFile(getMockMultipartFile());

        TopicImage topicImage = TopicImage.builder()
                .storedName(storedName)
                .originalName("defaultProfileImage.jfif")
                .build();

        topic.addTopicImage(topicImage);

        topicImageRepository.save(topicImage);

        // When / Then
        deleteAction(TOPIC_BASE_URL + "/{topicId}/images/{imageId}", true, topic.getId(), topicImage.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("토론 주제 아이디"),
                                        parameterWithName("imageId").description("이미지 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );
    }

    @DisplayName("이미지 삭제 시 작성자가 아닌 경우 403 에러를 반환한다.")
    @Test
    void deleteTopicImageFailWhenIsNotAuthor() throws Exception {
        // Given
        Member subMember = Member.builder()
                .accountId("tester2")
                .username("tester2")
                .password(passwordEncoder.encode("test2"))
                .email("test2@test.com")
                .build();
        memberRepository.save(subMember);

        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, subMember, category);

        TopicImage topicImage = TopicImage.builder()
                .storedName("storedName")
                .originalName("originalName")
                .build();
        topicImage.setTopic(topic);
        topicImageRepository.save(topicImage);

        // When / Then
        deleteAction(TOPIC_BASE_URL + "/{topicId}/images/{imageId}", true, topic.getId(), topicImage.getId())
                .andExpect(status().isForbidden());
    }

    @DisplayName("이미지 삭제 시 올바르지 않은 topicId를 전달할 경우 400 에러를 반환한다.")
    @Test
    void deleteTopicImageWithInvalidPathVariable1() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, member, category);

        // When / Then
        deleteAction(TOPIC_BASE_URL + "/{topicId}/images/{imageId}", true, topic.getId(), "hack")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.PATH_VAR_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.PATH_VAR_ERROR.getMessage()));
    }

    @Test
    @DisplayName("투표에 성공할 경우 204 응답을 반환한다.")
    void vote() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, member, category);

        VoteRequest req = VoteRequest.builder()
                .voteAt(LocalDateTime.of(2024, 1, 17, 10, 0))
                .voteType(VoteType.FIRST)
                .build();

        // When / Then
        patchAction(TOPIC_BASE_URL + "/{topicId}/vote", req, true, null, topic.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("토론 주제 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestFields(
                                        fieldWithPath("voteType").type(STRING).description("투표 타입")
                                                .attributes(withPath("voteType")),
                                        fieldWithPath("voteAt").type(STRING).description("투표 시각")
                                                .attributes(withPath("voteAt"))
                                )
                        )
                );

    }

    @Test
    @DisplayName("투표 시 투표 정보가 비어있을 경우 400 에러를 반환한다.")
    void voteWithoutVoteType() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, member, category);

        VoteRequest req = VoteRequest.builder()
                .voteAt(LocalDateTime.of(2024, 1, 17, 10, 0))
                .build();

        // When / Then
        patchAction(TOPIC_BASE_URL + "/{topicId}/vote", req, true, null, topic.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("투표 정보는 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("투표 시 투표 시각이 비어있을 경우 400 에러를 반환한다.")
    void voteWithoutVoteAt() throws Exception {
        // Given
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        Topic topic = createTopic("test", closedAt, now, member, category);

        VoteRequest req = VoteRequest.builder()
                .voteType(VoteType.FIRST)
                .build();

        // When / Then
        patchAction(TOPIC_BASE_URL + "/{topicId}/vote", req, true, null, topic.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("투표 시각은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("내가 작성한 주제 리스트 조회에 성공할 경우 200 응답과 함께 내가 작성한 주제 리스트의 페이징 정보를 반환한다.")
    void getMyTopics() throws Exception {
        Category category = createCategory();
        LocalDateTime now = LocalDateTime.of(2024, 1, 22, 10, 0);
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 23, 10, 0);
        createTopic("test", closedAt, now, member, category);
        createTopic("test", closedAt, now, member, category);
        createTopic("test", closedAt, now, member, category);

        getAction(TOPIC_BASE_URL + "/my", true, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("내가 작성한 주제 리스트 조회 성공"))
                .andExpect(jsonPath("$.data.isFirst").value(true))
                .andExpect(jsonPath("$.data.isLast").value(true))
                .andExpect(jsonPath("$.data.isEmpty").value(false))
                .andExpect(jsonPath("$.data.totalPage").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(3L))
                .andExpect(jsonPath("data.results.size()").value(3))
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("size").optional().description("조회 결과 크기"),
                                        parameterWithName("page").optional().description("페이지")
                                ),
                                pageResponseFields("MyTopicItem[]")
                                        .andWithPrefix("data.results[0].",
                                                fieldWithPath("topicId").type(NUMBER)
                                                        .description("주제 ID"),
                                                fieldWithPath("title").type(STRING)
                                                        .description("주제 제목"),
                                                fieldWithPath("viewCount").type(NUMBER)
                                                        .description("조회 수"),
                                                fieldWithPath("commentCount").type(NUMBER)
                                                        .description("댓글 수"),
                                                fieldWithPath("favoriteCount").type(NUMBER)
                                                        .description("좋아요 수"),
                                                fieldWithPath("voteCount").type(NUMBER)
                                                        .description("투표 수"),
                                                fieldWithPath("closedAt").type(STRING)
                                                        .description("토론 종료 시간"),
                                                fieldWithPath("createdAt").type(STRING)
                                                        .description("토론 생성 시간"),
                                                fieldWithPath("updatedAt").type(STRING)
                                                        .description("토론 수정 시간"),
                                                fieldWithPath("isClosed").type(BOOLEAN)
                                                        .description("토론 종료 여부"),
                                                fieldWithPath("categoryItem.id").type(NUMBER)
                                                        .description("카테고리 ID"),
                                                fieldWithPath("categoryItem.name").type(STRING)
                                                        .description("카테고리 이름"),
                                                fieldWithPath("categoryItem.createdAt").type(STRING)
                                                        .description("카테고리 생성 시각"),
                                                fieldWithPath("categoryItem.updatedAt").type(STRING)
                                                        .description("카테고리 수정 시각")
                                        )
                        )
                );

    }

    private MockMultipartFile getMockMultipartFile() throws IOException {
        final String originalFileName = "defaultProfileImage.jfif";
        final String filePath = "src/test/resources/static/img/" + originalFileName;

        return new MockMultipartFile(
                "images", //name
                originalFileName,
                "image/jpeg",
                new FileInputStream(filePath)
        );
    }


    private Topic createTopic(String title, LocalDateTime closedAt, LocalDateTime now, Member member, Category category) {
        Topic topic = Topic.builder()
                .title(title)
                .description("test")
                .closedAt(closedAt)
                .now(now)
                .firstChoice("first")
                .secondChoice("second")
                .author(member)
                .category(category)
                .build();

        return topicRepository.save(topic);
    }

    private Category createCategory() {
        Category category = Category.builder()
                .name("test")
                .build();

        return categoryRepository.save(category);
    }
}
