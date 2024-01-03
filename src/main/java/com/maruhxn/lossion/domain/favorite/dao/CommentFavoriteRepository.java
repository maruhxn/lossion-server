package com.maruhxn.lossion.domain.favorite.dao;

import com.maruhxn.lossion.domain.favorite.domain.CommentFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentFavoriteRepository extends JpaRepository<CommentFavorite, Long> {
}
