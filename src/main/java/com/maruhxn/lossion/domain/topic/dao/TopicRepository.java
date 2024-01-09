package com.maruhxn.lossion.domain.topic.dao;

import com.maruhxn.lossion.domain.topic.domain.Topic;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    @EntityGraph(attributePaths = {"author", "images", "category"})
    Optional<Topic> findTopicWithMemberAndImagesAndCategoryById(Long topicId);
}
