package com.maruhxn.lossion.domain.topic.dto.response;

import com.maruhxn.lossion.domain.topic.domain.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryItem {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public CategoryItem(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CategoryItem from(Category category) {
        return CategoryItem.builder()
                .id(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
