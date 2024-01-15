package com.maruhxn.lossion.domain.comment.dao;

import com.maruhxn.lossion.domain.comment.dto.response.CommentItem;
import com.maruhxn.lossion.domain.comment.dto.response.QCommentItem;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.maruhxn.lossion.domain.comment.domain.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory query;

    public Page<CommentItem> findTopLevelCommentsByTopicId(Long topicId, Pageable pageable) {

        List<CommentItem> commentItems = query
                .select(new QCommentItem(
                        comment.id,
                        comment.text,
                        comment.author,
                        comment.groupId,
                        comment.replyTo,
                        comment.createdAt,
                        comment.updatedAt
                ))
                .from(comment)
                .leftJoin(comment.replyTo)
                .where(comment.topic.id.eq(topicId).and(comment.replyTo.isNull()))
                .groupBy(comment.id, comment.replyTo)
                .orderBy(comment.createdAt.desc(), comment.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        JPAQuery<Long> countQuery = query
                .select(comment.count())
                .from(comment)
                .where(comment.topic.id.eq(topicId).and(comment.replyTo.isNull()));

        return PageableExecutionUtils.getPage(commentItems, pageable, countQuery::fetchOne);
    }

    public List<CommentItem> findRepliesByGroupId(Long topicId, String groupId) {
        return query
                .select(new QCommentItem(
                        comment.id,
                        comment.text,
                        comment.author,
                        comment.groupId,
                        comment.replyTo,
                        comment.createdAt,
                        comment.updatedAt
                ))
                .from(comment)
                .leftJoin(comment.replyTo)
                .where(comment.topic.id.eq(topicId).and(comment.groupId.eq(groupId)).and(comment.replyTo.isNotNull()))
                .orderBy(comment.createdAt.desc())
                .fetch();
    }
}
