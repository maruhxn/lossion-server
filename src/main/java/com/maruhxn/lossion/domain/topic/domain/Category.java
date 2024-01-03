package com.maruhxn.lossion.domain.topic.domain;

import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Category extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String name;

    public Category(String name) {
        this.name = name;
    }
}
