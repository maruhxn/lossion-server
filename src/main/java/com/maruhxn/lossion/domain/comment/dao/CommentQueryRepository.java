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
import static com.maruhxn.lossion.domain.favorite.domain.QCommentFavorite.commentFavorite;

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
                        commentFavorite.countDistinct(),
                        comment.replyTo.id,
                        comment.replies.size(),
                        comment.createdAt,
                        comment.updatedAt
                ))
                .from(comment)
                .leftJoin(comment.replyTo)
                .leftJoin(comment.favorites, commentFavorite)
                .where(comment.topic.id.eq(topicId).and(comment.replyTo.isNull()))
                .groupBy(comment.id)
                .orderBy(comment.createdAt.desc(), comment.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        JPAQuery<Long> countQuery = query
                .select(comment.count())
                .from(comment)
                .leftJoin(comment.replyTo)
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
                        commentFavorite.countDistinct(),
                        comment.replyTo.id,
                        comment.replies.size(),
                        comment.createdAt,
                        comment.updatedAt
                ))
                .from(comment)
                .leftJoin(comment.replyTo)
                .leftJoin(comment.favorites, commentFavorite)
                .where(comment.topic.id.eq(topicId).and(comment.groupId.eq(groupId)).and(comment.replyTo.isNotNull()))
                .groupBy(comment.id)
                .fetch();
    }
}
