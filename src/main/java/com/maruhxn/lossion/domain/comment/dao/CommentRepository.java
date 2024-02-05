package com.maruhxn.lossion.domain.comment.dao;

import com.maruhxn.lossion.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByTopicIdAndId(Long topicId, Long commentId);
}
