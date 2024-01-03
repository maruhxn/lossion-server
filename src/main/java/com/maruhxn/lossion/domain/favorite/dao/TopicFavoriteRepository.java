package com.maruhxn.lossion.domain.favorite.dao;

import com.maruhxn.lossion.domain.favorite.domain.TopicFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicFavoriteRepository extends JpaRepository<TopicFavorite, Long> {
}
