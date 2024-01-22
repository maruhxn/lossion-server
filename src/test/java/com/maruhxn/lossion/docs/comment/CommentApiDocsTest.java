package com.maruhxn.lossion.docs.comment;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.comment.dto.request.CreateCommentReq;
import com.maruhxn.lossion.domain.comment.dto.request.UpdateCommentReq;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.util.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.constraints.ConstraintDescriptions;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.maruhxn.lossion.global.common.Constants.ACCESS_TOKEN_HEADER;
import static com.maruhxn.lossion.global.common.Constants.REFRESH_TOKEN_HEADER;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Docs] - CommentAPIDocs")
public class CommentApiDocsTest extends RestDocsSupport {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글을 작성한다.")
    void createComment() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);

        CreateCommentReq req = CreateCommentReq.builder()
                .text("test")
                .build();
        simpleRequestConstraints = new ConstraintDescriptions(CreateCommentReq.class);

        // When / Then
        postAction("/api/topics/{topicId}/comments", req, true, topic.getId())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("댓글 생성 성공"))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("주제 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestFields(
                                        fieldWithPath("text").type(STRING).description("댓글 내용")
                                                .attributes(withPath("text")),
                                        fieldWithPath("replyToId").type(NUMBER).optional().description("답글 대상 댓글의 아이디")
                                                .attributes(withPath("replyToId"))
                                )
                        )
                );

    }

    @Test
    @DisplayName("댓글 작성 시 내용을 넘겨주지 않은 경우, 400 에러가 발생한다.")
    void createCommentWithoutText() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        CreateCommentReq req = CreateCommentReq.builder()
                .build();

        // When / Then
        postAction("/api/topics/{topicId}/comments", req, true, topic.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("message").value("올바르지 않은 입력입니다."))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("내용은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("로그인 하지 않은 사용자가 댓글 작성 요청 시, 401 에러가 발생한다.")
    void createCommentFailWhenIsNotLogin() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        CreateCommentReq req = CreateCommentReq.builder()
                .build();

        // When / Then
        postAction("/api/topics/{topicId}/comments", req, false, topic.getId())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 작성 요청 시, 해당 주제가 없다면 404가 발생한다.")
    void createCommentFailWhenNotFoundTopic() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        CreateCommentReq req = CreateCommentReq.builder()
                .text("text")
                .build();

        // When / Then
        postAction("/api/topics/{topicId}/comments", req, true, topic.getId() + 1)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상위 레벨 댓글만을 페이징 조회한다.")
    void getCommentList1() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment1 = createComment(topic, member);
        Comment comment2 = createComment(topic, member);

        // When / Then
        getAction("/api/topics/{topicId}/comments", false, null, topic.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("댓글 조회 성공"))
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("$.data.isFirst").value(true))
                .andExpect(jsonPath("$.data.isLast").value(true))
                .andExpect(jsonPath("$.data.isEmpty").value(false))
                .andExpect(jsonPath("$.data.totalPage").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.results.size()").value(2))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("주제 아이디")
                                ),
                                queryParameters(
                                        parameterWithName("size").optional().description("조회 결과 크기"),
                                        parameterWithName("page").optional().description("페이지")
                                ),
                                pageResponseFields("CommentItem[]")
                                        .andWithPrefix("data.results[0].",
                                                fieldWithPath("id").type(NUMBER)
                                                        .description("댓글 ID"),
                                                fieldWithPath("text").type(STRING)
                                                        .description("댓글 내용"),
                                                fieldWithPath("groupId").type(STRING)
                                                        .description("댓글이 속한 그룹의 UUID"),
                                                fieldWithPath("replyToId").type(NUMBER).optional()
                                                        .description("답글 대상 객체"),
                                                fieldWithPath("author.authorId").type(NUMBER)
                                                        .description("댓글 작성자 ID"),
                                                fieldWithPath("author.username").type(STRING)
                                                        .description("댓글 작성자 유저명"),
                                                fieldWithPath("author.profileImage").type(STRING)
                                                        .description("댓글 작성자 프로필 이미지"),
                                                fieldWithPath("favoriteCount").type(NUMBER)
                                                        .description("댓글 좋아요 수"),
                                                fieldWithPath("createdAt").type(STRING)
                                                        .description("댓글 생성 시각"),
                                                fieldWithPath("updatedAt").type(STRING)
                                                        .description("댓글 수정 시각")
                                        )
                        )
                );
    }

    @Test
    @DisplayName("상위 레벨 댓글만을 페이징 조회한다.")
    void getCommentList2() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = Comment.builder()
                .text("comment")
                .topic(topic)
                .author(member)
                .groupId(String.valueOf(UUID.randomUUID()))
                .build();

        Comment reply = Comment.builder()
                .text("reply")
                .topic(topic)
                .author(member)
                .build();

        comment.addReply(reply);
        commentRepository.save(comment);

        // When / Then
        getAction("/api/topics/{topicId}/comments", false, null, topic.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("댓글 조회 성공"))
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("$.data.isFirst").value(true))
                .andExpect(jsonPath("$.data.isLast").value(true))
                .andExpect(jsonPath("$.data.isEmpty").value(false))
                .andExpect(jsonPath("$.data.totalPage").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.results.size()").value(1));
    }

    @Test
    @DisplayName("댓글을 수정한다.")
    void updateComment() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);
        UpdateCommentReq req = UpdateCommentReq.builder()
                .text("test")
                .build();

        simpleRequestConstraints = new ConstraintDescriptions(UpdateCommentReq.class);

        // When / Then
        patchAction("/api/topics/{topicId}/comments/{commentId}", req, true, null, topic.getId(), comment.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("주제 아이디"),
                                        parameterWithName("commentId").description("댓글 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                ),
                                requestFields(
                                        fieldWithPath("text").type(STRING).description("댓글 수정 내용")
                                                .attributes(withPath("text"))
                                )
                        )
                );
    }

    @Test
    @DisplayName("댓글 수정 시 내용을 넘겨주지 않은 경우, 400 에러가 발생한다.")
    void updateCommentWithoutText() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);
        UpdateCommentReq req = UpdateCommentReq.builder()
                .build();

        // When / Then
        patchAction("/api/topics/{topicId}/comments/{commentId}", req, true, null, topic.getId(), comment.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("message").value("올바르지 않은 입력입니다."))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("내용은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("댓글 수정 시 내용을 해당 댓글이 없을 경우, 404 에러가 발생한다.")
    void updateCommentFailWhenNotFoundTopic() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);
        UpdateCommentReq req = UpdateCommentReq.builder()
                .text("text")
                .build();

        // When / Then
        patchAction("/api/topics/{topicId}/comments/{commentId}", req, true, null, topic.getId(), comment.getId() + 1)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 댓글 수정 요청 시, 401 에러가 발생한다.")
    void updateCommentFailWhenIsNotLogin() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);
        UpdateCommentReq req = UpdateCommentReq.builder()
                .text("text")
                .build();

        // When / Then
        patchAction("/api/topics/{topicId}/comments/{commentId}", req, false, null, topic.getId(), comment.getId())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 수정 시 내용을 해당 주제가 없을 경우, 404 에러가 발생한다.")
    void updateCommentFailWhenNotFoundComment() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);
        UpdateCommentReq req = UpdateCommentReq.builder()
                .text("text")
                .build();

        // When / Then
        patchAction("/api/topics/{topicId}/comments/{commentId}", req, true, null, topic.getId() + 1, comment.getId())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 작성자가 아닌 다른 사용자가 수정 요청을 하는 경우, 403 에러가 발생한다.")
    void updateCommentFailWhenIsNotAuthor() throws Exception {
        // Given
        Member member2 = Member.builder()
                .accountId("tester2")
                .username("tester2")
                .password(passwordEncoder.encode("test"))
                .telNumber("01000000002")
                .email("test2@test.com")
                .build();
        memberRepository.save(member2);

        Category category = createCategory();
        Topic topic = createTopic(member2, category);
        Comment comment = createComment(topic, member2);
        UpdateCommentReq req = UpdateCommentReq.builder()
                .text("text")
                .build();

        // When / Then
        patchAction("/api/topics/{topicId}/comments/{commentId}", req, true, null, topic.getId(), comment.getId())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    void deleteComment() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        // When / Then
        deleteAction("/api/topics/{topicId}/comments/{commentId}", true, topic.getId(), comment.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("주제 아이디"),
                                        parameterWithName("commentId").description("댓글 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );
    }

    @Test
    @DisplayName("댓글 삭제 시 내용을 해당 주제가 없을 경우, 404 에러가 발생한다.")
    void deleteCommentFailWhenNotFoundComment() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        // When / Then
        deleteAction("/api/topics/{topicId}/comments/{commentId}", true, topic.getId() + 1, comment.getId())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 삭제 시 내용을 해당 댓글이 없을 경우, 404 에러가 발생한다.")
    void deleteCommentFailWhenNotFoundTopic() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        // When / Then
        deleteAction("/api/topics/{topicId}/comments/{commentId}", true, topic.getId(), comment.getId() + 1)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 댓글 삭제 요청 시, 401 에러가 발생한다.")
    void deleteCommentFailWhenIsNotLogin() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        // When / Then
        deleteAction("/api/topics/{topicId}/comments/{commentId}", false, topic.getId(), comment.getId())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 작성자가 아닌 다른 사용자가 삭제 요청을 하는 경우, 403 에러가 발생한다.")
    void deleteCommentFailWhenIsNotAuthor() throws Exception {
        // Given
        Member member2 = Member.builder()
                .accountId("tester2")
                .username("tester2")
                .password(passwordEncoder.encode("test"))
                .telNumber("01000000002")
                .email("test2@test.com")
                .build();
        memberRepository.save(member2);

        Category category = createCategory();
        Topic topic = createTopic(member2, category);
        Comment comment = createComment(topic, member2);

        // When / Then
        deleteAction("/api/topics/{topicId}/comments/{commentId}", true, topic.getId(), comment.getId())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("답글을 조회한다.")
    void getReplies() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = Comment.builder()
                .text("comment")
                .topic(topic)
                .author(member)
                .groupId(String.valueOf(UUID.randomUUID()))
                .build();

        Comment reply = Comment.builder()
                .text("reply")
                .topic(topic)
                .author(member)
                .build();

        comment.addReply(reply);
        commentRepository.save(comment);

        // When / Then
        getAction("/api/topics/{topicId}/comments/groups/{groupId}", false, null, topic.getId(), comment.getGroupId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("답글 조회 성공"))
                .andExpect(jsonPath("data").isArray())
                .andExpect(jsonPath("data[0].id").value(reply.getId()))
                .andExpect(jsonPath("data[0].text").value("reply"))
                .andExpect(jsonPath("data[0].groupId").value(comment.getGroupId()))
                .andExpect(jsonPath("data[0].replyToId").value(comment.getId()))
                .andExpect(jsonPath("data[0].author.authorId").value(member.getId()))
                .andExpect(jsonPath("data[0].author.username").value(member.getUsername()))
                .andExpect(jsonPath("data[0].author.profileImage").value(member.getProfileImage()))
                .andExpect(jsonPath("data[0].favoriteCount").value(0L))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("주제 아이디"),
                                        parameterWithName("groupId").description("댓글 그룹 아이디")
                                ),
                                commonResponseFields("CommentItem[]")
                                        .andWithPrefix("data[0].",
                                                fieldWithPath("id").type(NUMBER)
                                                        .description("댓글 ID"),
                                                fieldWithPath("text").type(STRING)
                                                        .description("댓글 내용"),
                                                fieldWithPath("groupId").type(STRING)
                                                        .description("댓글이 속한 그룹의 UUID"),
                                                fieldWithPath("replyToId").type(NUMBER).optional()
                                                        .description("답글 대상 객체 ID"),
                                                fieldWithPath("author.authorId").type(NUMBER)
                                                        .description("댓글 작성자 ID"),
                                                fieldWithPath("author.username").type(STRING)
                                                        .description("댓글 작성자 유저명"),
                                                fieldWithPath("author.profileImage").type(STRING)
                                                        .description("댓글 작성자 프로필 이미지"),
                                                fieldWithPath("favoriteCount").type(NUMBER)
                                                        .description("댓글 좋아요 수"),
                                                fieldWithPath("createdAt").type(STRING)
                                                        .description("댓글 생성 시각"),
                                                fieldWithPath("updatedAt").type(STRING)
                                                        .description("댓글 수정 시각"))
                        )
                );
    }

    private Comment createComment(Topic topic, Member member) {
        Comment comment = Comment.builder()
                .text("comment")
                .topic(topic)
                .author(member)
                .groupId(String.valueOf(UUID.randomUUID()))
                .build();
        return commentRepository.save(comment);
    }

    private Topic createTopic(Member member, Category category) {
        Topic topic = Topic.builder()
                .title("test")
                .description("test")
                .closedAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .now(LocalDateTime.of(2024, 1, 14, 10, 0))
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
