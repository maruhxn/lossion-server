package com.maruhxn.lossion.domain.favorite.dao;

import com.maruhxn.lossion.domain.favorite.domain.TopicFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicFavoriteRepository extends JpaRepository<TopicFavorite, Long> {

    Optional<TopicFavorite> findByTopic_IdAndMember_Id(Long topicId, Long memberId);

    boolean existsByTopic_IdAndMember_Id(Long topicId, Long memberId);
}
