package com.maruhxn.lossion.domain.topic.domain;

import com.maruhxn.lossion.domain.topic.dto.request.CreateCategoryReq;
import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Category extends BaseEntity {

    @Column(nullable = false, length = 30, unique = true)
    private String name;

    @Builder
    public Category(String name) {
        Assert.hasText(name, "카테고리명은 필수입니다.");
        this.name = name;
    }

    public static Category from(CreateCategoryReq req) {
        return Category.builder()
                .name(req.getName())
                .build();
    }

    // 편의 메서드 //
    public void updateName(String name) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
    }
}
