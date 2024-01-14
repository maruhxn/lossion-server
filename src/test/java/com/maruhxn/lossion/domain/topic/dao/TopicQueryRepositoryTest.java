package com.maruhxn.lossion.domain.topic.dao;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@SpringBootTest
class TopicQueryRepositoryTest {

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

    @AfterEach
    void tearDown() {
        voteRepository.deleteAll();
        topicRepository.deleteAll();
        categoryRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("아무 검색 조건을 전달하지 않으면 가장 최신의 주제 10개를 페이징하여 전달한다.")
    void findAllByConditions() {
        // Given
        Member member = Member.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password("test")
                .telNumber("01012345678")
                .build();
        memberRepository.save(member);

        Category category = Category.builder()
                .name("test")
                .build();

        categoryRepository.save(category);

        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);

        Topic topic1 = Topic.builder()
                .title("test1")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(member)
                .category(category)
                .build();

        Topic topic2 = Topic.builder()
                .title("test2")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(member)
                .category(category)
                .build();

        topicRepository.saveAll(List.of(topic1, topic2));

        Vote vote = Vote.builder()
                .voteType(VoteType.FIRST)
                .voter(member)
                .topic(topic1)
                .build();

        voteRepository.save(vote);
        TopicSearchCond cond = TopicSearchCond.builder()
                .build();

        AuthorInfoItem authorInfoItem = AuthorInfoItem.from(member);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<TopicItem> findTopics = topicQueryRepository.findAllByConditions(cond, pageRequest);

        // Then
        assertThat(findTopics).hasSize(2)
                .extracting("topicId", "title", "viewCount", "author", "commentCount", "favoriteCount", "closedAt", "isClosed", "voteCount")
                .containsExactlyInAnyOrder(
                        tuple(topic2.getId(), "test2", 0L, authorInfoItem, 0L, 0L, closedAt, false, 0L),
                        tuple(topic1.getId(), "test1", 0L, authorInfoItem, 0L, 0L, closedAt, false, 1L)
                );
    }

    @Test
    @DisplayName("제목 키워드를 전달하면 제목에 해당 키워드를 포함하는 가장 최신의 주제 10개를 페이징하여 전달한다.")
    void findAllByConditionsWithTitleCond() {
        // Given
        Member member = Member.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password("test")
                .telNumber("01012345678")
                .build();
        memberRepository.save(member);

        Category category = Category.builder()
                .name("test")
                .build();

        categoryRepository.save(category);

        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);

        Topic topic1 = Topic.builder()
                .title("test1")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(member)
                .category(category)
                .build();

        Topic topic2 = Topic.builder()
                .title("test2")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(member)
                .category(category)
                .build();

        topicRepository.saveAll(List.of(topic1, topic2));

        Vote vote = Vote.builder()
                .voteType(VoteType.FIRST)
                .voter(member)
                .topic(topic1)
                .build();

        voteRepository.save(vote);
        TopicSearchCond cond = TopicSearchCond.builder()
                .title("1")
                .build();

        AuthorInfoItem authorInfoItem = AuthorInfoItem.from(member);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<TopicItem> findTopics = topicQueryRepository.findAllByConditions(cond, pageRequest);

        // Then
        assertThat(findTopics).hasSize(1)
                .extracting("topicId", "title", "viewCount", "author", "commentCount", "favoriteCount", "closedAt", "isClosed", "voteCount")
                .containsExactlyInAnyOrder(
                        tuple(topic1.getId(), "test1", 0L, authorInfoItem, 0L, 0L, closedAt, false, 1L)
                );
    }

    @Test
    @DisplayName("멤버 아이디를 전달하면 해당 멤버가 작성한 주제들을 확인한다.")
    void findMyTopics() {
        // Given
        Member member = Member.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password("test")
                .telNumber("01012345678")
                .build();
        memberRepository.save(member);

        Category category = Category.builder()
                .name("test")
                .build();

        categoryRepository.save(category);

        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);

        Topic topic1 = Topic.builder()
                .title("test1")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(member)
                .category(category)
                .build();

        Topic topic2 = Topic.builder()
                .title("test2")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(member)
                .category(category)
                .build();

        topicRepository.saveAll(List.of(topic1, topic2));

        Vote vote = Vote.builder()
                .voteType(VoteType.FIRST)
                .voter(member)
                .topic(topic1)
                .build();

        voteRepository.save(vote);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<MyTopicItem> myTopics = topicQueryRepository.findMyTopics(member.getId(), pageRequest);

        // Then
        assertThat(myTopics).hasSize(2)
                .extracting("topicId", "title", "viewCount", "commentCount", "favoriteCount", "voteCount", "closedAt", "isClosed")
                .containsExactlyInAnyOrder(
                        tuple(topic2.getId(), "test2", 0L, 0L, 0L, 0L, closedAt, false),
                        tuple(topic1.getId(), "test1", 0L, 0L, 0L, 1L, closedAt, false)
                );
    }
}