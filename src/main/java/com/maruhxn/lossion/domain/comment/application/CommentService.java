package com.maruhxn.lossion.domain.comment.application;

import com.maruhxn.lossion.domain.comment.dao.CommentQueryRepository;
import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.comment.dto.request.CreateCommentReq;
import com.maruhxn.lossion.domain.comment.dto.request.UpdateCommentReq;
import com.maruhxn.lossion.domain.comment.dto.response.CommentItem;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.global.common.dto.PageItem;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final TopicRepository topicRepository;

    @Transactional
    public void createComment(Member author, Long topicId, CreateCommentReq req, String groupId) {
        Topic findTopic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOPIC));

        Comment comment = Comment.of(author, findTopic, req, groupId);

        if (req.getReplyToId() != null) {
            Comment replyTo = commentRepository.findById(req.getReplyToId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_COMMENT));
            replyTo.addReply(comment);
        }

        commentRepository.save(comment);
    }

    public PageItem getTopLevelComments(Long topicId, Pageable pageable) {
        Page<CommentItem> topLevelComments = commentQueryRepository.findTopLevelCommentsByTopicId(topicId, pageable);
        return PageItem.from(topLevelComments);
    }

    @Transactional
    public void updateComment(Long topicId, Long commentId, UpdateCommentReq req) {
        Comment findComment = commentRepository.findByTopicIdAndId(topicId, commentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_COMMENT));

        findComment.updateText(req.getText());
    }

    @Transactional
    public void deleteComment(Long topicId, Long commentId) {
        Comment findComment = commentRepository.findByTopicIdAndId(topicId, commentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_COMMENT));

        commentRepository.delete(findComment);
    }

    public List<CommentItem> getRepliesByGroupId(Long topicId, String groupId) {
        return commentQueryRepository.findRepliesByGroupId(topicId, groupId);
    }
}
