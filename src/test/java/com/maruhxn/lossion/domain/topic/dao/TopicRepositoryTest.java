package com.maruhxn.lossion.domain.topic.dao;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.favorite.dao.TopicFavoriteRepository;
import com.maruhxn.lossion.domain.favorite.domain.TopicFavorite;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.domain.*;
import com.maruhxn.lossion.util.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Repository] - TopicRepository")
class TopicRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TopicFavoriteRepository topicFavoriteRepository;

    @Test
    @DisplayName("주제의 상세 정보를 조회한다.")
    void findTopicWithMemberAndImagesAndCategoryAndVotesById() {
        // Given
        Member member = createMember();
        Category category = createCategory();

        Topic topic = createTopic("test", member, category);

        Vote vote = createVote(member, topic);
        topic.addVote(vote);
        Comment comment1 = createComment(topic, member);
        Comment comment2 = createComment(topic, member);
        Comment comment3 = createComment(topic, member);
        topic.addComment(comment1);
        topic.addComment(comment2);
        topic.addComment(comment3);

        TopicFavorite topicFavorite = createTopicFavorite(topic, member);
        topic.addFavorite(topicFavorite);

        // When
        Topic findTopic = topicRepository.findTopicWithMemberAndCategoryAndVotesById(topic.getId()).get();

        List<TopicImage> images = findTopic.getImages();
        List<Comment> comments = findTopic.getComments();
        List<Vote> votes = findTopic.getVotes();
        List<TopicFavorite> favorites = findTopic.getFavorites();
        // Then
        assertThat(images).isEmpty();
        assertThat(comments).containsExactlyInAnyOrder(comment1, comment2, comment3);
        assertThat(votes).containsExactly(vote);
        assertThat(favorites).containsExactly(topicFavorite);
    }

    private TopicFavorite createTopicFavorite(Topic topic1, Member member) {
        TopicFavorite topicFavorite = TopicFavorite.builder()
                .topic(topic1)
                .member(member)
                .build();
        return topicFavoriteRepository.save(topicFavorite);
    }

    private Vote createVote(Member member, Topic topic1) {
        Vote vote = Vote.builder()
                .voteType(VoteType.FIRST)
                .voter(member)
                .topic(topic1)
                .build();

        return voteRepository.save(vote);
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

    private Topic createTopic(String title, Member member, Category category) {
        Topic topic = Topic.builder()
                .title(title)
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