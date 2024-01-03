package com.maruhxn.lossion.domain.comment.domain;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@DisplayName("엔티티 - Comment")
class CommentTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("Comment 순환 참조 테스트")
    void shouldHaveChildWhenReplyToParent() {
        // Given
        Member member = Member.builder()
                .accountId("id")
                .email("test@test.com")
                .password("test")
                .username("tester")
                .build();

        Category category = new Category("test");

        Topic topic = Topic.builder()
                .title("test")
                .description("test")
                .author(member)
                .category(category)
                .build();

        Comment parent = Comment.builder()
                .author(member)
                .content("parent")
                .topic(topic)
                .build();

        Comment child = Comment.builder()
                .author(member)
                .content("child")
                .topic(topic)
                .build();

        parent.addReply(child);
        topic.addComment(parent);

        // When
        memberRepository.save(member);
        categoryRepository.save(category);
        commentRepository.save(parent);
        topicRepository.save(topic);

        // Then
        assertThat(topic.getComments().size()).isEqualTo(2);
        assertThat(parent.getReplies()).containsExactly(child);
        assertThat(child.getParent()).isEqualTo(parent);
    }
}