package com.maruhxn.lossion.domain.favorite.application;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.favorite.dao.CommentFavoriteRepository;
import com.maruhxn.lossion.domain.favorite.dao.TopicFavoriteRepository;
import com.maruhxn.lossion.domain.favorite.domain.CommentFavorite;
import com.maruhxn.lossion.domain.favorite.domain.TopicFavorite;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.util.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Service] - FavoriteService")
class FavoriteServiceTest extends IntegrationTestSupport {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TopicFavoriteRepository topicFavoriteRepository;

    @Autowired
    private CommentFavoriteRepository commentFavoriteRepository;

    @DisplayName("주제 좋아요 - 좋아요를 누르면 엔티티가 생성된다.")
    @Test
    void topicFavorite() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        // When
        favoriteService.topicFavorite(topic.getId(), member);

        // Then
        TopicFavorite topicFavorite = topicFavoriteRepository.findByTopic_IdAndMember_Id(topic.getId(), member.getId()).get();
        assertThat(topicFavorite).isNotNull();
    }

    @DisplayName("주제 좋아요 - 좋아요를 취소하면 엔티티가 삭제된다.")
    @Test
    void undoTopicFavorite() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        TopicFavorite topicFavorite = TopicFavorite.builder()
                .member(member)
                .topic(topic)
                .build();
        topicFavoriteRepository.save(topicFavorite);

        // When
        favoriteService.topicFavorite(topic.getId(), member);

        // Then
        Optional<TopicFavorite> optionalTopicFav = topicFavoriteRepository.findByTopic_IdAndMember_Id(topic.getId(), member.getId());
        assertThat(optionalTopicFav.isEmpty()).isTrue();
    }

    @DisplayName("댓글 좋아요 - 좋아요를 누르면 엔티티가 생성된다.")
    @Test
    void commentFavorite() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);
        // When
        favoriteService.commentFavorite(comment.getId(), member);

        // Then
        Optional<CommentFavorite> optionalCommentFavorite = commentFavoriteRepository.findByComment_IdAndMember_Id(comment.getId(), member.getId());
        assertThat(optionalCommentFavorite.isPresent()).isTrue();
    }

    @DisplayName("댓글 좋아요 - 좋아요를 취소하면 엔티티가 삭제된다.")
    @Test
    void undoCommentFavorite() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);
        CommentFavorite commentFavorite = CommentFavorite.builder()
                .member(member)
                .comment(comment)
                .build();
        commentFavoriteRepository.save(commentFavorite);

        // When
        favoriteService.commentFavorite(comment.getId(), member);

        // Then
        Optional<CommentFavorite> optionalCommentFav = commentFavoriteRepository.findByComment_IdAndMember_Id(comment.getId(), member.getId());
        assertThat(optionalCommentFav.isEmpty()).isTrue();
    }

    @DisplayName("주제 좋아요가 존재하지 않을 경우 EntityNotFoundException 발생")
    @Test
    void checkTopicFavoriteWithNoEntity() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);

        // When / Then
        assertThatThrownBy(() -> favoriteService.checkTopicFavorite(topic.getId(), member))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_FAVORITE.getMessage());
    }

    @DisplayName("댓글 좋아요가 존재하지 않을 경우 EntityNotFoundException 발생")
    @Test
    void checkCommentFavoriteWithNoEntity() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        // When / Then
        assertThatThrownBy(() -> favoriteService.checkCommentFavorite(comment.getId(), member))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_FAVORITE.getMessage());
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

    private Member createMember() {
        Member member = Member.builder()
                .accountId("tester")
                .email("test@test.com")
                .username("tester")
                .password("test")
                .telNumber("01000000000")
                .build();

        return memberRepository.save(member);
    }

    private Category createCategory() {
        Category category = Category.builder()
                .name("test")
                .build();

        return categoryRepository.save(category);
    }
}