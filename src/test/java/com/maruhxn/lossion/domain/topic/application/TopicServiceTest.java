package com.maruhxn.lossion.domain.topic.application;

import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.dao.VoteRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.domain.topic.domain.Vote;
import com.maruhxn.lossion.domain.topic.domain.VoteType;
import com.maruhxn.lossion.domain.topic.dto.request.VoteRequest;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.common.dto.PageItem;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Transactional
class TopicServiceTest {
    @Autowired
    private TopicService topicService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private VoteRepository voteRepository;

    @DisplayName("기존 투표 정보가 없을 때 투표를 하는 경우, 투표 정보는 요청한 투표 정보와 같다.")
    @Test
    void vote() {
        // Given
        Member member = Member.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password("test")
                .telNumber("01012345678")
                .build();
        Member savedMember = memberRepository.save(member);

        Category category = Category.builder()
                .name("test")
                .build();
        Category savedCategory = categoryRepository.save(category);

        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);
        Topic topic = Topic.builder()
                .title("test")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(savedMember)
                .category(savedCategory)
                .build();

        Topic savedTopic = topicRepository.save(topic);

        VoteRequest request = VoteRequest.builder()
                .voteType(VoteType.FIRST)
                .voteAt(closedAt.minusDays(1))
                .build();

        // When
        Vote vote = topicService.vote(savedTopic.getId(), savedMember.getId(), request);

        // Then
        assertThat(vote.getVoteType()).isEqualTo(request.getVoteType());
    }

    @DisplayName("기존과 다른 투표 정보로 재투표를 하는 경우, 해당 정보로 변경된다.")
    @Test
    void voteWithDiffVoteType() {
        // Given
        Member member = Member.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password("test")
                .telNumber("01012345678")
                .build();
        Member savedMember = memberRepository.save(member);

        Category category = Category.builder()
                .name("test")
                .build();
        Category savedCategory = categoryRepository.save(category);

        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);
        Topic topic = Topic.builder()
                .title("test")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(savedMember)
                .category(savedCategory)
                .build();

        Topic savedTopic = topicRepository.save(topic);

        Vote vote = Vote.builder()
                .voter(savedMember)
                .topic(savedTopic)
                .voteType(VoteType.FIRST)
                .build();
        voteRepository.save(vote);

        VoteRequest request = VoteRequest.builder()
                .voteType(VoteType.SECOND)
                .voteAt(closedAt.minusDays(1))
                .build();

        // When
        Vote resultVote = topicService.vote(savedTopic.getId(), savedMember.getId(), request);

        // Then
        assertThat(resultVote.getVoteType()).isEqualTo(resultVote.getVoteType());
    }

    @DisplayName("기존과 같은 투표 정보로 재투표를 하는 경우, null이 저장된다.")
    @Test
    void voteWithSameVoteType() {
        // Given
        Member member = Member.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password("test")
                .telNumber("01012345678")
                .build();
        Member savedMember = memberRepository.save(member);

        Category category = Category.builder()
                .name("test")
                .build();
        Category savedCategory = categoryRepository.save(category);

        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);
        Topic topic = Topic.builder()
                .title("test")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(savedMember)
                .category(savedCategory)
                .build();

        Topic savedTopic = topicRepository.save(topic);

        Vote vote = Vote.builder()
                .voter(savedMember)
                .topic(savedTopic)
                .voteType(VoteType.FIRST)
                .build();
        voteRepository.save(vote);

        VoteRequest request = VoteRequest.builder()
                .voteType(VoteType.FIRST)
                .voteAt(closedAt.minusDays(1))
                .build();

        // When
        Vote resultVote = topicService.vote(savedTopic.getId(), savedMember.getId(), request);

        // Then
        assertThat(resultVote.getVoteType()).isNull();
    }

    @DisplayName("이미 종료된 토론에 투표하는 경우 400 에러가 발생한다.")
    @Test
    void voteOnOverTime() {
        // Given
        Member member = Member.builder()
                .accountId("tester")
                .username("tester")
                .email("test@test.com")
                .password("test")
                .telNumber("01012345678")
                .build();
        Member savedMember = memberRepository.save(member);

        Category category = Category.builder()
                .name("test")
                .build();
        Category savedCategory = categoryRepository.save(category);

        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);
        Topic topic = Topic.builder()
                .title("test")
                .description("test")
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(savedMember)
                .category(savedCategory)
                .build();

        Topic savedTopic = topicRepository.save(topic);

        VoteRequest request = VoteRequest.builder()
                .voteType(VoteType.FIRST)
                .voteAt(closedAt.plusDays(1))
                .build();

        // When / Then
        assertThatThrownBy(() -> topicService.vote(savedTopic.getId(), savedMember.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.ALREADY_CLOSED.getMessage());
    }

    @DisplayName("유저 정보와 PageRequest를 전달하면, 해당 유저가 작성한 주제를 페이징하여 제공한다.")
    @Test
    void getMyTopics() {
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
        Topic topic = Topic.builder()
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

        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        PageItem myTopics = topicService.getMyTopics(jwtMemberInfo, pageRequest);

        // Then
        assertThat(myTopics)
                .extracting("results", "isFirst", "isLast", "isEmpty", "totalPage", "totalElements")
                .contains(myTopics.getResults(), true, true, false, 1, 1);
        assertThat(myTopics.getResults()).hasSize(1)
                .extracting("topicId", "title", "viewCount", "commentCount", "favoriteCount", "voteCount", "closedAt", "isClosed")
                .containsExactlyInAnyOrder(
                        tuple(topic.getId(), "test", 0L, 0L, 0L, 0L, closedAt, false)
                );
    }
}