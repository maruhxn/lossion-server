package com.maruhxn.lossion.domain.comment.application;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.comment.dto.request.CreateCommentReq;
import com.maruhxn.lossion.domain.comment.dto.request.UpdateCommentReq;
import com.maruhxn.lossion.domain.comment.dto.response.CommentItem;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.global.common.dto.PageItem;
import com.maruhxn.lossion.util.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("[Service] - CommentService")
class CommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;


    @DisplayName("댓글을 작성한다.")
    @Test
    void createComment() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);

        String groupId = String.valueOf(UUID.randomUUID());
        CreateCommentReq req = CreateCommentReq.builder()
                .text("test")
                .build();

        // When
        commentService.createComment(member, topic.getId(), req, groupId);

        // Then
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(1)
                .extracting("text", "author", "topic", "replyTo", "groupId")
                .containsExactlyInAnyOrder(
                        tuple("test", member, topic, null, groupId)
                );
    }

    @DisplayName("댓글 작성 시 내용이 null이면 에러가 발생한다.")
    @Test
    void createCommentWithoutText() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        String groupId = String.valueOf(UUID.randomUUID());

        CreateCommentReq req = CreateCommentReq.builder()
                .build();

        // When / Then
        assertThatThrownBy(() -> commentService.createComment(member, topic.getId(), req, groupId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("내용은 필수입니다.");
    }

    @DisplayName("답글을 작성한다.")
    @Test
    void createReply() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        String groupId = String.valueOf(UUID.randomUUID());
        Comment parent = Comment.builder()
                .text("parent")
                .topic(topic)
                .author(member)
                .groupId(groupId)
                .build();
        commentRepository.save(parent);

        CreateCommentReq req = CreateCommentReq.builder()
                .text("child")
                .replyToId(parent.getId())
                .build();

        // When
        commentService.createComment(member, topic.getId(), req, groupId);

        // Then
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(2)
                .extracting("text", "author", "topic", "replyTo", "groupId")
                .containsExactlyInAnyOrder(
                        tuple("parent", member, topic, null, groupId),
                        tuple("child", member, topic, parent, groupId)
                );
    }

    @DisplayName("최신 10개의 부모 댓글 리스트를 불러온다.")
    @Test
    void getTopLevelComments() {
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

        List<Comment> comments = commentRepository.saveAll(topLevelComments);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        PageItem topLevelCommentsPageItem = commentService.getTopLevelComments(topic.getId(), pageRequest);
        // Then
//        List<CommentItem> result = new ArrayList<>();
//
//        for (int i = comments.size(); i > 0; i--) {
//            result.add(CommentItem.from(comments.get(i - 1)));
//        }

        assertThat(topLevelCommentsPageItem)
                .extracting("results", "isFirst", "isLast", "isEmpty", "totalPage", "totalElements")
                .contains(topLevelCommentsPageItem.getResults(), true, true, false, 1, (long) topLevelComments.size());
    }

    @DisplayName("부모 댓글이 없을 경우 빈 배열을 반환한다.")
    @Test
    void getTopLevelCommentsWhenEmpty() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        PageItem topLevelCommentsPageItem = commentService.getTopLevelComments(topic.getId(), pageRequest);

        // Then
        assertThat(topLevelCommentsPageItem)
                .extracting("results", "isFirst", "isLast", "isEmpty", "totalPage", "totalElements")
                .contains(List.of(), true, true, true, 0, 0);
    }

    @DisplayName("답글을 조회한다.")
    @Test
    void getCommentsWithNoComments() {
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

        // When
        List<CommentItem> replies = commentService.getRepliesByGroupId(topic.getId(), comment.getGroupId());

        // Then
        assertThat(replies).hasSize(3)
                .extracting("id", "replyToId", "groupId")
                .containsExactlyInAnyOrder(
                        tuple(reply1.getId(), comment.getId(), comment.getGroupId()),
                        tuple(reply2.getId(), reply1.getId(), comment.getGroupId()),
                        tuple(reply3.getId(), comment.getId(), comment.getGroupId())
                );
    }

    @DisplayName("댓글 내용을 수정한다.")
    @Test
    void updateComment() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        commentRepository.save(comment);
        UpdateCommentReq req = UpdateCommentReq.builder()
                .text("test!")
                .build();
        // When
        commentService.updateComment(topic.getId(), comment.getId(), req);

        // Then
        assertThat(comment)
                .extracting("text", "author", "topic", "replyTo")
                .contains("test!", member, topic, null);
    }

    @DisplayName("댓글을 삭제한다.")
    @Test
    void deleteComment() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        Topic topic = createTopic(member, category);
        Comment comment = createComment(topic, member);

        commentRepository.save(comment);
        // When
        commentService.deleteComment(topic.getId(), comment.getId());

        // Then
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).isEmpty();
    }

    @DisplayName("댓글을 삭제하면, 해당 댓글의 답글까지 모두 삭제된다.")
    @Test
    void deleteCommentWithCascadeAll1() {
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
        // When
        commentService.deleteComment(topic.getId(), reply1.getId());

        // Then
        assertThatThrownBy(() -> commentRepository.findById(reply2.getId()).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("댓글을 삭제하면, 해당 댓글의 답글까지 모두 삭제된다.")
    @Test
    void deleteCommentWithCascadeAll2() {
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
        // When
        commentService.deleteComment(topic.getId(), comment.getId());

        // Then
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).isEmpty();
    }

    private Comment createComment(Topic topic, Member member) {
        return Comment.builder()
                .text("comment")
                .topic(topic)
                .author(member)
                .groupId(String.valueOf(UUID.randomUUID()))
                .build();
    }

    private static Comment createReply(Topic topic, Member member) {
        return Comment.builder()
                .text("reply")
                .topic(topic)
                .author(member)
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