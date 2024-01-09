package com.maruhxn.lossion.domain.topic.dao;

import com.maruhxn.lossion.domain.topic.domain.QCategory;
import com.maruhxn.lossion.domain.topic.dto.request.TopicSearchCond;
import com.maruhxn.lossion.domain.topic.dto.response.QTopicItem;
import com.maruhxn.lossion.domain.topic.dto.response.TopicItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.maruhxn.lossion.domain.comment.domain.QComment.comment;
import static com.maruhxn.lossion.domain.favorite.domain.QTopicFavorite.topicFavorite;
import static com.maruhxn.lossion.domain.member.domain.QMember.member;
import static com.maruhxn.lossion.domain.topic.domain.QCategory.category;
import static com.maruhxn.lossion.domain.topic.domain.QTopic.topic;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class TopicQueryRepository {
    private final JPAQueryFactory query;

    public Page<TopicItem> findAllByConditions(TopicSearchCond cond, Pageable pageable) {
        List<TopicItem> topicItems = query
                .select(new QTopicItem(
                        topic.id,
                        category,
                        topic.title,
                        topic.viewCount,
                        topic.author,
                        comment.count(),
                        topicFavorite.count(),
                        topic.createdAt,
                        topic.closedAt,
                        topic.isClosed
                ))
                .from(topic)
                .leftJoin(topic.category, category)
                .leftJoin(topic.author, member)
                .leftJoin(topic.comments, comment)
                .leftJoin(topic.favorites, topicFavorite)
                .where(containTitleKeyword(cond.getTitle()),
                        containContentKeyword(cond.getDescription()),
                        authorLike(cond.getAuthor()))
                .groupBy(topic.id, category, topic.title, topic.viewCount, topic.author, topic.createdAt, topic.closedAt, topic.isClosed)
                .orderBy(topic.createdAt.desc(), topic.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(topic.count())
                .from(topic)
                .where(containTitleKeyword(cond.getTitle()),
                        containContentKeyword(cond.getDescription()),
                        authorLike(cond.getAuthor()));

        return PageableExecutionUtils.getPage(topicItems, pageable, countQuery::fetchOne);
    }

    private BooleanExpression containTitleKeyword(String title) {
        return hasText(title) ? topic.title.contains(title) : null;
    }

    private BooleanExpression containContentKeyword(String content) {
        return hasText(content) ? topic.description.contains(content) : null;
    }

    private static BooleanExpression authorLike(String authorName) {
        return hasText(authorName) ? topic.author.username.eq(authorName) : null;
    }
}
