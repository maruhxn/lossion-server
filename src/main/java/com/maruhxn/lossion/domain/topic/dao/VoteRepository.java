package com.maruhxn.lossion.domain.topic.dao;

import com.maruhxn.lossion.domain.topic.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByTopicIdAndVoter_Id(Long topicId, Long voterId);
}
