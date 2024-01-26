package com.maruhxn.lossion.global.auth.checker;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.maruhxn.lossion.domain.member.domain.Role.ROLE_ADMIN;

@Component
@RequiredArgsConstructor
public class AuthChecker {

    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;

    public boolean isSelf(Long memberId) {
        CustomUserDetails principal = getPrincipal();
        return principal.getId().equals(memberId);
    }

    public boolean isVerified() {
        CustomUserDetails principal = getPrincipal();
        if (!principal.isEnabled()) {
            throw new ForbiddenException(ErrorCode.UNVERIFIED_EMAIL);
        }
        return true;
    }

    public boolean isTopicAuthor(Long topicId) {
        CustomUserDetails principal = getPrincipal();
        if (!principal.isEnabled()) {
            throw new ForbiddenException(ErrorCode.UNVERIFIED_EMAIL);
        }

        Topic findTopic = topicRepository.findById(topicId).orElseThrow(
                () -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOPIC));

        if (!principal.getAuthorities().contains(ROLE_ADMIN)
                && !principal.getId().equals(findTopic.getAuthor().getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        return true;
    }

    public boolean isCommentAuthor(Long commentId) {
        CustomUserDetails principal = getPrincipal();
        if (!principal.isEnabled()) {
            throw new ForbiddenException(ErrorCode.UNVERIFIED_EMAIL);
        }

        Comment findComment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException(ErrorCode.NOT_FOUND_COMMENT));

        if (!principal.getAuthorities().contains(ROLE_ADMIN)
                && !principal.getId().equals(findComment.getAuthor().getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        return true;
    }

    private static CustomUserDetails getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

}
