package com.maruhxn.lossion.domain.topic.dao;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.favorite.dao.TopicFavoriteRepository;
import com.maruhxn.lossion.domain.favorite.domain.TopicFavorite;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.domain.topic.domain.Vote;
import com.maruhxn.lossion.domain.topic.domain.VoteType;
import com.maruhxn.lossion.domain.topic.dto.request.TopicSearchCond;
import com.maruhxn.lossion.domain.topic.dto.response.AuthorInfoItem;
import com.maruhxn.lossion.domain.topic.dto.response.MyTopicItem;
import com.maruhxn.lossion.domain.topic.dto.response.TopicItem;
import com.maruhxn.lossion.util.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DisplayName("[레포지토리] - TopicQueryRepository")
class TopicQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private TopicQueryRepository topicQueryRepository;

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
    @DisplayName("아무 검색 조건을 전달하지 않으면 가장 최신의 주제 10개를 페이징하여 전달한다.")
    void findAllByConditions() {
        // Given
        Member member = createMember();
        Category category = createCategory();

        Topic topic1 = createTopic("test1", member, category);
        Topic topic2 = createTopic("test2", member, category);

        createVote(member, topic1);
        createComment(topic1, member);
        createComment(topic1, member);
        createComment(topic1, member);
        createComment(topic2, member);

        createTopicFavorite(topic1, member);
        createTopicFavorite(topic1, member);
        createTopicFavorite(topic2, member);

        TopicSearchCond cond = TopicSearchCond.builder()
                .build();

        AuthorInfoItem authorInfoItem = AuthorInfoItem.from(member);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<TopicItem> findTopics = topicQueryRepository.findAllByConditions(cond, pageRequest);

        // Then
        assertThat(findTopics).hasSize(2)
                .extracting("topicId", "title", "viewCount", "author", "commentCount", "favoriteCount", "isClosed", "voteCount")
                .containsExactlyInAnyOrder(
                        tuple(topic2.getId(), "test2", 0L, authorInfoItem, 1L, 1L, false, 0L),
                        tuple(topic1.getId(), "test1", 0L, authorInfoItem, 3L, 2L, false, 1L)
                );
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

    @Test
    @DisplayName("제목 키워드를 전달하면 제목에 해당 키워드를 포함하는 가장 최신의 주제 10개를 페이징하여 전달한다.")
    void findAllByConditionsWithTitleCond() {
        // Given
        Member member = createMember();
        Category category = createCategory();

        Topic topic1 = createTopic("test1", member, category);

        createVote(member, topic1);
        createComment(topic1, member);

        createTopicFavorite(topic1, member);
        createTopicFavorite(topic1, member);

        TopicSearchCond cond = TopicSearchCond.builder()
                .title("1")
                .build();

        AuthorInfoItem authorInfoItem = AuthorInfoItem.from(member);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<TopicItem> findTopics = topicQueryRepository.findAllByConditions(cond, pageRequest);

        // Then
        assertThat(findTopics).hasSize(1)
                .extracting("topicId", "title", "viewCount", "author", "commentCount", "favoriteCount", "isClosed", "voteCount")
                .containsExactlyInAnyOrder(
                        tuple(topic1.getId(), "test1", 0L, authorInfoItem, 1L, 2L, false, 1L)
                );
    }

    @Test
    @DisplayName("멤버 아이디를 전달하면 해당 멤버가 작성한 주제들을 확인한다.")
    void findMyTopics() {
        // Given
        Member member = createMember();
        Category category = createCategory();

        Topic topic1 = createTopic("test1", member, category);
        Topic topic2 = createTopic("test2", member, category);

        createComment(topic1, member);
        createComment(topic1, member);
        createComment(topic2, member);

        createTopicFavorite(topic1, member);
        createTopicFavorite(topic1, member);

        createVote(member, topic1);
        createVote(member, topic2);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<MyTopicItem> myTopics = topicQueryRepository.findMyTopics(member.getId(), pageRequest);

        // Then
        assertThat(myTopics).hasSize(2)
                .extracting("topicId", "title", "viewCount", "commentCount", "favoriteCount", "voteCount", "isClosed")
                .containsExactlyInAnyOrder(
                        tuple(topic2.getId(), "test2", 0L, 1L, 0L, 1L, false),
                        tuple(topic1.getId(), "test1", 0L, 2L, 2L, 1L, false)
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