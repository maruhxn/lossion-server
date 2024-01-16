package com.maruhxn.lossion.domain.favorite.dao;

import com.maruhxn.lossion.domain.favorite.domain.CommentFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentFavoriteRepository extends JpaRepository<CommentFavorite, Long> {

    Optional<CommentFavorite> findByComment_IdAndMember_Id(Long topicId, Long memberId);

    boolean existsByComment_IdAndMember_Id(Long commentId, Long id);
}
