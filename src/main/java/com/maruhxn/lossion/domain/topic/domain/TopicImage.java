package com.maruhxn.lossion.domain.topic.domain;

import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopicImage extends BaseEntity {

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String storedName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @Builder
    public TopicImage(String originalName, String storedName) {
        Assert.hasText(originalName, "원본 이름은 필수입니다.");
        Assert.hasText(storedName, "저장된 이름은 필수입니다.");

        this.originalName = originalName;
        this.storedName = storedName;
    }

    // 편의 메서드 //
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

}
