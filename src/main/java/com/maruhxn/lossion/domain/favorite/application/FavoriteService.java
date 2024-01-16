package com.maruhxn.lossion.domain.favorite.application;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.favorite.dao.CommentFavoriteRepository;
import com.maruhxn.lossion.domain.favorite.dao.TopicFavoriteRepository;
import com.maruhxn.lossion.domain.favorite.domain.CommentFavorite;
import com.maruhxn.lossion.domain.favorite.domain.TopicFavorite;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteService {

    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;
    private final TopicFavoriteRepository topicFavoriteRepository;
    private final CommentFavoriteRepository commentFavoriteRepository;

    @Transactional
    public void topicFavorite(Long topicId, Member member) {
        Topic findTopic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOPIC));

        Optional<TopicFavorite> optionalTopicFav = topicFavoriteRepository.findByTopic_IdAndMember_Id(topicId, member.getId());
        boolean isEmpty = optionalTopicFav.isEmpty();

        if (isEmpty) {
            TopicFavorite topicFavorite = TopicFavorite.builder()
                    .member(member)
                    .topic(findTopic)
                    .build();
            topicFavoriteRepository.save(topicFavorite);
        } else {
            topicFavoriteRepository.delete(optionalTopicFav.get());
        }
    }

    @Transactional
    public void commentFavorite(Long commentId, Member member) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_COMMENT));

        Optional<CommentFavorite> optionalCommentFav = commentFavoriteRepository.findByComment_IdAndMember_Id(commentId, member.getId());
        boolean isEmpty = optionalCommentFav.isEmpty();

        if (isEmpty) {
            CommentFavorite commentFavorite = CommentFavorite.builder()
                    .member(member)
                    .comment(findComment)
                    .build();
            commentFavoriteRepository.save(commentFavorite);
        } else {
            commentFavoriteRepository.delete(optionalCommentFav.get());
        }
    }

    public void checkTopicFavorite(Long topicId, Member member) {
        boolean isExist = topicFavoriteRepository.existsByTopic_IdAndMember_Id(topicId, member.getId());
        if (!isExist) {
            throw new EntityNotFoundException(ErrorCode.NOT_FOUND_FAVORITE);
        }
    }

    public void checkCommentFavorite(Long commentId, Member member) {
        boolean isExist = commentFavoriteRepository.existsByComment_IdAndMember_Id(commentId, member.getId());
        if (!isExist) {
            throw new EntityNotFoundException(ErrorCode.NOT_FOUND_FAVORITE);
        }
    }
}
