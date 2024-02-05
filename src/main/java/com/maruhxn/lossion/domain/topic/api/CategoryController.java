package com.maruhxn.lossion.domain.topic.api;

import com.maruhxn.lossion.domain.topic.application.CategoryService;
import com.maruhxn.lossion.domain.topic.dto.request.CreateCategoryReq;
import com.maruhxn.lossion.domain.topic.dto.request.UpdateCategoryReq;
import com.maruhxn.lossion.domain.topic.dto.response.CategoryItem;
import com.maruhxn.lossion.global.common.dto.BaseResponse;
import com.maruhxn.lossion.global.common.dto.DataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<DataResponse<List<CategoryItem>>> getAllCategories() {
        List<CategoryItem> result = categoryService.getAll();
        return ResponseEntity.ok(DataResponse.of("카테고리 조회 성공", result));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> createCategory(
            @RequestBody @Valid CreateCategoryReq req
    ) {
        categoryService.createCategory(req);
        return new ResponseEntity<>(new BaseResponse("카테고리 생성 성공"), HttpStatus.CREATED);
    }

    @PatchMapping("/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody @Valid UpdateCategoryReq req
    ) {
        categoryService.updateCategory(categoryId, req);
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable("categoryId") Long categoryId
    ) {
        log.info("카테고리 삭제 | categoryId={}", categoryId);
        categoryService.deleteCategory(categoryId);
    }
}
