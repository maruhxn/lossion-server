package com.maruhxn.lossion.domain.topic.dao;

import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.domain.topic.dto.request.TopicSearchCond;
import com.maruhxn.lossion.domain.topic.dto.response.MyTopicItem;
import com.maruhxn.lossion.domain.topic.dto.response.QMyTopicItem;
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
import java.util.Optional;

import static com.maruhxn.lossion.domain.comment.domain.QComment.comment;
import static com.maruhxn.lossion.domain.favorite.domain.QTopicFavorite.topicFavorite;
import static com.maruhxn.lossion.domain.member.domain.QMember.member;
import static com.maruhxn.lossion.domain.topic.domain.QCategory.category;
import static com.maruhxn.lossion.domain.topic.domain.QTopic.topic;
import static com.maruhxn.lossion.domain.topic.domain.QVote.vote;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class TopicQueryRepository {
    private final JPAQueryFactory query;

    public Optional<Topic> findTopicDetail(Long topicId) {
        return Optional.ofNullable(query
                .select(topic)
                .from(topic)
                .leftJoin(topic.author, member)
                .leftJoin(topic.category, category)
                .leftJoin(topic.comments, comment)
                .leftJoin(topic.votes, vote).on(vote.voteType.isNotNull())
                .leftJoin(topic.favorites, topicFavorite)
                .where(topic.id.eq(topicId))
                .groupBy(topic.id)
                .fetchOne());
    }

    public Page<TopicItem> findAllByConditions(TopicSearchCond cond, Pageable pageable) {
        List<TopicItem> topicItems = query
                .select(new QTopicItem(
                        topic.id,
                        category,
                        topic.title,
                        topic.viewCount,
                        vote.countDistinct(),
                        topic.author,
                        comment.countDistinct(),
                        topicFavorite.countDistinct(),
                        topic.createdAt,
                        topic.closedAt,
                        topic.isClosed
                ))
                .from(topic)
                .leftJoin(topic.category, category)
                .leftJoin(topic.votes, vote).on(vote.voteType.isNotNull())
                .leftJoin(topic.author, member)
                .leftJoin(topic.comments, comment)
                .leftJoin(topic.favorites, topicFavorite)
                .where(containTitleKeyword(cond.getTitle()),
                        containContentKeyword(cond.getDescription()),
                        authorLike(cond.getAuthor()))
                .groupBy(topic.id)
                .orderBy(topic.createdAt.desc(), topic.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(topic.count())
                .from(topic)
                .leftJoin(topic.votes, vote)
                .leftJoin(topic.comments, comment)
                .leftJoin(topic.favorites, topicFavorite)
                .where(containTitleKeyword(cond.getTitle()),
                        containContentKeyword(cond.getDescription()),
                        authorLike(cond.getAuthor()));

        return PageableExecutionUtils.getPage(topicItems, pageable, countQuery::fetchOne);
    }

    public Page<MyTopicItem> findMyTopics(Long memberId, Pageable pageable) {
        List<MyTopicItem> myTopicItems = query
                .select(new QMyTopicItem(
                        topic.id,
                        category,
                        topic.title,
                        topic.viewCount,
                        comment.countDistinct(),
                        topicFavorite.countDistinct(),
                        vote.countDistinct(),
                        topic.createdAt,
                        topic.updatedAt,
                        topic.closedAt,
                        topic.isClosed
                ))
                .from(topic)
                .leftJoin(topic.category, category)
                .leftJoin(topic.votes, vote).on(vote.voteType.isNotNull())
                .leftJoin(topic.comments, comment)
                .leftJoin(topic.favorites, topicFavorite)
                .where(topic.author.id.eq(memberId))
                .groupBy(topic.id)
                .orderBy(topic.createdAt.desc(), topic.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(topic.count())
                .from(topic)
                .leftJoin(topic.votes, vote)
                .leftJoin(topic.comments, comment)
                .leftJoin(topic.favorites, topicFavorite)
                .where(topic.author.id.eq(memberId));

        return PageableExecutionUtils.getPage(myTopicItems, pageable, countQuery::fetchOne);
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
