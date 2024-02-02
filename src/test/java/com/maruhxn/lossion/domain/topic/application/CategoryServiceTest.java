package com.maruhxn.lossion.domain.topic.application;

import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.dto.request.CreateCategoryReq;
import com.maruhxn.lossion.domain.topic.dto.request.UpdateCategoryReq;
import com.maruhxn.lossion.domain.topic.dto.response.CategoryItem;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.AlreadyExistsResourceException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.util.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Service] - CategoryService")
class CategoryServiceTest extends IntegrationTestSupport {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("전체 카테고리를 조회한다.")
    @Test
    void getAll() {
        // Given
        Category category1 = createCategory("test1");
        Category category2 = createCategory("test2");
        Category category3 = createCategory("test3");
        Category category4 = createCategory("test4");
        categoryRepository.saveAll(List.of(category1, category2, category3, category4));

        // When
        List<CategoryItem> categoryItems = categoryService.getAll();

        // Then
        assertThat(categoryItems).hasSize(4)
                .extracting("name")
                .contains("test1", "test2", "test3", "test4");

    }

    @DisplayName("카테고리를 생성한다.")
    @Test
    void createCategory() {
        // Given
        CreateCategoryReq req = CreateCategoryReq.builder()
                .name("test")
                .build();

        // When
        Category category = categoryService.createCategory(req);

        // Then
        assertThat(category)
                .extracting("name")
                .isEqualTo("test");
    }

    @DisplayName("카테고리를 생성 시 이미 존재하는 이름이면 에러가 발생한다.")
    @Test
    void createCategoryWithExistingName() {
        // Given
        Category existingCategory = createCategory("test");
        categoryRepository.save(existingCategory);

        CreateCategoryReq req = CreateCategoryReq.builder()
                .name("test")
                .build();

        // When / Then
        assertThatThrownBy(() -> categoryService.createCategory(req))
                .isInstanceOf(AlreadyExistsResourceException.class)
                .hasMessage(ErrorCode.EXISTING_CATEGORY.getMessage());
    }

    @DisplayName("카테고리를 생성 시 이름이 30자를 넘기면 에러가 발생한다.")
    @Test
    void createCategoryWithOverLengthName() {
        // Given
        CreateCategoryReq req = CreateCategoryReq.builder()
                .name("testfasdfasdfasdvasdvasdfasdfasdfasvasdrfqwdfasdfasdvasdvasdvasdqr")
                .build();

        // When / Then
        assertThatThrownBy(() -> categoryService.createCategory(req))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("카테고리를 생성 시 카테고리 이름을 전달하지 않으면 에러가 발생한다.")
    @Test
    void createCategoryWithoutName() {
        // Given
        CreateCategoryReq req = CreateCategoryReq.builder()
                .build();

        // When / Then
        assertThatThrownBy(() -> categoryService.createCategory(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리명은 필수입니다.");
    }

    @DisplayName("카테고리를 수정한다.")
    @Test
    void updateCategory() {
        // Given
        Category category = createCategory("test");
        categoryRepository.save(category);
        UpdateCategoryReq req = UpdateCategoryReq.builder()
                .name("test!!")
                .build();

        // When
        categoryService.updateCategory(category.getId(), req);

        // Then
        Category findCategory = categoryRepository.findById(category.getId()).get();
        assertThat(findCategory)
                .extracting("name")
                .isEqualTo("test!!");
    }

    @DisplayName("카테고리를 수정 시 존재하지 않는 카테고리이면 에러가 발생한다.")
    @Test
    void updateCategoryWithNonExistingCategory() {
        // Given
        UpdateCategoryReq req = UpdateCategoryReq.builder()
                .name("test!!")
                .build();

        // When / Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, req))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_CATEGORY.getMessage());
    }

    @DisplayName("카테고리를 수정 시 카테고리 이름을 전달하지 않으면 수정되지 않는다.")
    @Test
    void updateCategoryWithoutName() {
        // Given
        Category category = createCategory("test");
        categoryRepository.save(category);
        UpdateCategoryReq req = UpdateCategoryReq.builder()
                .build();

        // When
        categoryService.updateCategory(category.getId(), req);
        // Then
        Category findCategory = categoryRepository.findById(category.getId()).get();
        assertThat(findCategory).isEqualTo(category);
    }

    @DisplayName("카테고리를 삭제한다.")
    @Test
    void deleteCategory() {
        // Given
        Category category = createCategory("test");
        categoryRepository.save(category);

        // When
        categoryService.deleteCategory(category.getId());

        // Then
        Optional<Category> optional = categoryRepository.findById(category.getId());
        assertThat(optional.isEmpty()).isTrue();
    }

    @DisplayName("카테고리를 수정 시 존재하지 않는 카테고리이면 에러가 발생한다.")
    @Test
    void deleteCategoryWithNonExistingCategory() {
        // Given

        // When / Then
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_CATEGORY.getMessage());
    }

    private Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .build();
    }

    private Member createMember() {
        Member member = Member.builder()
                .accountId("tester1")
                .username("tester1")
                .email("test1@test.com")
                .password("test1")
                .build();
        return memberRepository.save(member);
    }

}