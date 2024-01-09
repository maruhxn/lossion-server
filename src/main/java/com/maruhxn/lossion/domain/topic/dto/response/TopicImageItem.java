package com.maruhxn.lossion.domain.topic.dto.response;

import com.maruhxn.lossion.domain.topic.domain.TopicImage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopicImageItem {
    private Long imageId;
    private String originalName;
    private String storedName;

    @Builder
    public TopicImageItem(Long imageId, String originalName, String storedName) {
        this.imageId = imageId;
        this.originalName = originalName;
        this.storedName = storedName;
    }

    public static TopicImageItem from(TopicImage topicImage) {
        return TopicImageItem.builder()
                .imageId(topicImage.getId())
                .originalName(topicImage.getOriginalName())
                .storedName(topicImage.getStoredName())
                .build();
    }
}
