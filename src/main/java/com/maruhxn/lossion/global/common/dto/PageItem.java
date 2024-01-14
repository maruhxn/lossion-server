package com.maruhxn.lossion.global.common.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageItem<T> {
    private List<T> results;
    private Boolean isFirst;
    private Boolean isLast;
    private Boolean isEmpty;
    private int totalPage;
    private Long totalElements;

    @Builder
    public PageItem(List<T> results, Boolean isFirst, Boolean isLast, Boolean isEmpty, int totalPage, Long totalElements) {
        this.results = results;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.isEmpty = isEmpty;
        this.totalPage = totalPage;
        this.totalElements = totalElements;
    }

    public static <T> PageItem from(Page<T> page) {
        return PageItem.builder()
                .results((List<Object>) page.getContent())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .isEmpty(page.isEmpty())
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements()).build();
    }
}
