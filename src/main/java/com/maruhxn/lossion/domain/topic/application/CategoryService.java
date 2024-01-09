package com.maruhxn.lossion.domain.topic.application;

import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.dto.request.CreateCategoryReq;
import com.maruhxn.lossion.domain.topic.dto.request.UpdateCategoryReq;
import com.maruhxn.lossion.domain.topic.dto.response.CategoryItem;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.AlreadyExistsResourceException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryItem> getAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryItem::from)
                .toList();
    }

    public void createCategory(CreateCategoryReq req) {
        uniqueCheck(req);
        Category category = Category.from(req);
        categoryRepository.save(category);
    }

    private void uniqueCheck(CreateCategoryReq req) {
        Boolean isExist = categoryRepository.existsByName(req.getName());
        if (isExist) {
            throw new AlreadyExistsResourceException(ErrorCode.EXISTING_CATEGORY);
        }
    }

    public void updateCategory(Long categoryId, UpdateCategoryReq req) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_CATEGORY));

        findCategory.updateName(req.getName());
    }

    public void deleteCategory(Long categoryId) {
        boolean isExist = categoryRepository.existsById(categoryId);
        if (!isExist) {
            throw new EntityNotFoundException(ErrorCode.NOT_FOUND_CATEGORY);
        }
        categoryRepository.deleteById(categoryId);
    }
}
