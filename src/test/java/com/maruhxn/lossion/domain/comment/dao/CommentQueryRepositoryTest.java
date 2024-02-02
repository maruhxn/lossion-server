package com.maruhxn.lossion.domain.comment.dao;

import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.comment.dto.response.CommentItem;
import com.maruhxn.lossion.domain.favorite.dao.CommentFavoriteRepository;
import com.maruhxn.lossion.domain.favorite.domain.CommentFavorite;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.util.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DisplayName("[Repository] - CommentQueryRepository")
class CommentQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private CommentQueryRepository commentQueryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentFavoriteRepository commentFavoriteRepository;

    @DisplayName("부모 레벨의 댓글을 생성 시간 순으로 페이징 조회한다.")
    @Test
    void findTopLevelComments() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);

        Comment comment1 = createComment(topic, member);
        Comment comment2 = createComment(topic, member);
        Comment comment3 = createComment(topic, member);
        Comment comment4 = createComment(topic, member);
        Comment comment5 = createComment(topic, member);

        Comment replyToComment1 = Comment.builder()
                .text("reply")
                .topic(topic)
                .author(member)
                .build();

        comment1.addReply(replyToComment1);

        List<Comment> topLevelComments = List.of(comment1, comment2, comment3, comment4, comment5);

        commentRepository.saveAll(topLevelComments);

        CommentFavorite commentFavorite = CommentFavorite.builder()
                .comment(comment1)
                .member(member)
                .build();

        commentFavoriteRepository.save(commentFavorite);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<CommentItem> topLevelCommentsPageItem = commentQueryRepository
                .findTopLevelCommentsByTopicId(topic.getId(), pageRequest);

        // Then
        assertThat(topLevelCommentsPageItem).hasSize(5)
                .extracting("id", "favoriteCount", "replyToId", "groupId")
                .containsExactlyInAnyOrder(
                        tuple(comment5.getId(), 0L, null, comment5.getGroupId()),
                        tuple(comment4.getId(), 0L, null, comment4.getGroupId()),
                        tuple(comment3.getId(), 0L, null, comment3.getGroupId()),
                        tuple(comment2.getId(), 0L, null, comment2.getGroupId()),
                        tuple(comment1.getId(), 1L, null, comment1.getGroupId())
                );

    }

    @DisplayName("답글을 조회한다.")
    @Test
    void findRepliesByGroupId() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);

        Comment comment = createComment(topic, member);

        Comment reply1 = createReply(topic, member);
        Comment reply2 = createReply(topic, member);
        Comment reply3 = createReply(topic, member);

        comment.addReply(reply1);
        reply1.addReply(reply2);
        comment.addReply(reply3);

        commentRepository.save(comment);

        CommentFavorite reply1Favorite = CommentFavorite.builder()
                .comment(reply1)
                .member(member)
                .build();

        commentFavoriteRepository.save(reply1Favorite);
        // When
        List<CommentItem> replies = commentQueryRepository
                .findRepliesByGroupId(topic.getId(), comment.getGroupId());

        // Then
        assertThat(replies).hasSize(3)
                .extracting("id", "favoriteCount", "replyToId", "groupId")
                .containsExactlyInAnyOrder(
                        tuple(reply1.getId(), 1L, comment.getId(), comment.getGroupId()),
                        tuple(reply2.getId(), 0L, reply1.getId(), comment.getGroupId()),
                        tuple(reply3.getId(), 0L, comment.getId(), comment.getGroupId())
                );

    }

    private static Comment createReply(Topic topic, Member member) {
        return Comment.builder()
                .text("reply")
                .topic(topic)
                .author(member)
                .build();
    }

    private Comment createComment(Topic topic, Member member) {
        return Comment.builder()
                .text("comment")
                .topic(topic)
                .author(member)
                .groupId(String.valueOf(UUID.randomUUID()))
                .build();
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