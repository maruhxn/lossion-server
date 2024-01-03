package com.maruhxn.lossion.domain.topic.dao;

import com.maruhxn.lossion.domain.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
