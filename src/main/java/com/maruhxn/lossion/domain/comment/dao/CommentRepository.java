package com.maruhxn.lossion.domain.comment.dao;

import com.maruhxn.lossion.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
