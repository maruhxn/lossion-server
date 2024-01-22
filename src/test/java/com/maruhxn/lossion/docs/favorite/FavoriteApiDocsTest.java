package com.maruhxn.lossion.docs.favorite;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.favorite.dao.CommentFavoriteRepository;
import com.maruhxn.lossion.domain.favorite.dao.TopicFavoriteRepository;
import com.maruhxn.lossion.domain.favorite.domain.CommentFavorite;
import com.maruhxn.lossion.domain.favorite.domain.TopicFavorite;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.util.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.maruhxn.lossion.global.common.Constants.ACCESS_TOKEN_HEADER;
import static com.maruhxn.lossion.global.common.Constants.REFRESH_TOKEN_HEADER;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Docs] - FavoriteAPIDocs")
public class FavoriteApiDocsTest extends RestDocsSupport {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TopicFavoriteRepository topicFavoriteRepository;

    @Autowired
    private CommentFavoriteRepository commentFavoriteRepository;

    @DisplayName("해당 주제에 좋아요를 남긴다.")
    @Test
    void topicFavorite() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);

        // When / Then
        patchAction("/api/favorites/topics/{topicId}", null, true, null, topic.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("주제 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );
    }

    @DisplayName("주제 좋아요 요청 시 해당 주제가 없다면 404를 반환한다.")
    @Test
    void topicFavoriteFailWhenNotFoundTopic() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);

        // When / Then
        patchAction("/api/favorites/topics/{topicId}", null, true, null, topic.getId() + 1)
                .andExpect(status().isNotFound());
    }

    @DisplayName("주제 좋아요 요청 시 로그인 하지 않은 사용자는 401 에러를 반환한다.")
    @Test
    void topicFavoriteFailWhenIsNotLogin() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);

        // When / Then
        patchAction("/api/favorites/topics/{topicId}", null, false, null, topic.getId())
                .andExpect(status().isUnauthorized());
    }


    @DisplayName("해당 주제의 좋아요 여부를 확인한다.")
    @Test
    void checkTopicFavorite() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        TopicFavorite topicFavorite = TopicFavorite.builder()
                .member(member)
                .topic(topic)
                .build();
        topicFavoriteRepository.save(topicFavorite);

        // When / Then
        getAction("/api/favorites/topics/{topicId}", true, null, topic.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("topicId").description("주제 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );
    }

    @DisplayName("주제 좋아요 여부 확인 시 해당 주제가 없으면 404 에러를 반환한다.")
    @Test
    void checkTopicFavoriteFailWhenIsNotFoundTopic() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        TopicFavorite topicFavorite = TopicFavorite.builder()
                .member(member)
                .topic(topic)
                .build();
        topicFavoriteRepository.save(topicFavorite);

        // When / Then
        getAction("/api/favorites/topics/{topicId}", true, null, topic.getId() + 1)
                .andExpect(status().isNotFound());
    }

    @DisplayName("주제 좋아요 여부 확인 시 로그인 하지 않은 경우 401 에러를 반환한다.")
    @Test
    void checkTopicFavoriteFailWhenIsNotLogin() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        TopicFavorite topicFavorite = TopicFavorite.builder()
                .member(member)
                .topic(topic)
                .build();
        topicFavoriteRepository.save(topicFavorite);

        // When / Then
        getAction("/api/favorites/topics/{topicId}", false, null, topic.getId())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("댓글에 좋아요를 남긴다.")
    @Test
    void commentFavorite() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        // When / Then
        patchAction("/api/favorites/comments/{commentId}", null, true, null, comment.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("commentId").description("댓글 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );
    }

    @DisplayName("댓글 좋아요 요청 시 해당 댓글이 없으면 404 에러를 반환한다.")
    @Test
    void commentFavoriteFailWhenIsNotFoundComment() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        // When / Then
        patchAction("/api/favorites/comments/{commentId}", null, true, null, comment.getId() + 1)
                .andExpect(status().isNotFound());
    }

    @DisplayName("댓글 좋아요 요청 시 로그인 하지 않은 경우 401 에러를 반환한다.")
    @Test
    void commentFavoriteFailWhenIsNotLogin() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        // When / Then
        patchAction("/api/favorites/comments/{commentId}", null, false, null, comment.getId())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("댓글의 좋아요 여부를 확인한다.")
    @Test
    void checkCommentFavorite() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        CommentFavorite commentFavorite = CommentFavorite.builder()
                .member(member)
                .comment(comment)
                .build();
        commentFavoriteRepository.save(commentFavorite);

        // When / Then
        getAction("/api/favorites/comments/{commentId}", true, null, comment.getId())
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("commentId").description("댓글 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(ACCESS_TOKEN_HEADER).description("인증 토큰 헤더"),
                                        headerWithName(REFRESH_TOKEN_HEADER).description("Refresh 토큰 헤더")
                                )
                        )
                );
    }

    @DisplayName("댓글 좋아요 여부 확인 시 해당 댓글이 없으면 404 에러를 반환한다.")
    @Test
    void checkCommentFavoriteFailWhenIsNotFoundComment() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        CommentFavorite commentFavorite = CommentFavorite.builder()
                .member(member)
                .comment(comment)
                .build();
        commentFavoriteRepository.save(commentFavorite);

        // When / Then
        getAction("/api/favorites/comments/{commentId}", true, null, comment.getId() + 1)
                .andExpect(status().isNotFound());
    }

    @DisplayName("댓글 좋아요 여부 확인 시 로그인 하지 않은 경우 401 에러를 반환한다.")
    @Test
    void checkCommentFavoriteFailWhenIsNotLogin() throws Exception {
        // Given
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        CommentFavorite commentFavorite = CommentFavorite.builder()
                .member(member)
                .comment(comment)
                .build();
        commentFavoriteRepository.save(commentFavorite);

        // When / Then
        getAction("/api/favorites/comments/{commentId}", false, null, comment.getId())
                .andExpect(status().isUnauthorized());
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
