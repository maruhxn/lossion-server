package com.maruhxn.lossion.domain.topic.dao;

import com.maruhxn.lossion.domain.topic.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
