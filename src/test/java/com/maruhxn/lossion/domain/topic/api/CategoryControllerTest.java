package com.maruhxn.lossion.domain.topic.api;

import com.maruhxn.lossion.domain.topic.dto.request.CreateCategoryReq;
import com.maruhxn.lossion.domain.topic.dto.request.UpdateCategoryReq;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.util.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[컨트롤러] - CategoryController")
class CategoryControllerTest extends ControllerTestSupport {

    @DisplayName("카테고리 전체 조회")
    @Test
    @WithMockUser
    void getAllCategories() throws Exception {
        // Given

        // When / Then
        mockMvc.perform(
                        get("/api/categories")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("카테고리 조회 성공"))
                .andExpect(jsonPath("data").isArray());

    }

    @DisplayName("카테고리 생성")
    @Test
    @WithMockUser
    void createCategory() throws Exception {
        // Given
        CreateCategoryReq req = CreateCategoryReq.builder()
                .name("test")
                .build();

        // When / Then
        mockMvc.perform(
                        post("/api/categories")
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("카테고리 생성 성공"));


    }

    @DisplayName("카테고리 생성 시 카테고리 이름이 비어있으면 400 에러")
    @Test
    @WithMockUser
    void createCategoryWithoutCategoryName() throws Exception {
        // Given
        CreateCategoryReq req = CreateCategoryReq.builder()
                .build();

        // When / Then
        mockMvc.perform(
                        post("/api/categories")
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("카테고리명은 비어있을 수 없습니다."));

    }

    @DisplayName("카테고리 생성 시 카테고리 이름이 30자를 넘는다면 400 에러")
    @Test
    @WithMockUser
    void createCategoryWithOverLengthCategoryName() throws Exception {
        // Given
        CreateCategoryReq req = CreateCategoryReq.builder()
                .name("asdfkhlasdkvasdlkjvakldsvaklsdhvaskldvklasdhvaklsdvhalskdvaksdhl")
                .build();

        // When / Then
        mockMvc.perform(
                        post("/api/categories")
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("카테고리명은 최대 30 글자입니다."));

    }

    @DisplayName("카테고리 업데이트")
    @Test
    @WithMockUser
    void updateCategory() throws Exception {
        // Given
        UpdateCategoryReq req = UpdateCategoryReq.builder()
                .name("test")
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/categories/{categoryId}", 1)
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

    }

    @DisplayName("카테고리 업데이트 시 카테고리 이름이 비어있으면 400 에러")
    @Test
    @WithMockUser
    void updateCategoryWithoutCategoryName() throws Exception {
        // Given
        UpdateCategoryReq req = UpdateCategoryReq.builder()
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/categories/{categoryId}", 1)
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("카테고리명은 비어있을 수 없습니다."));

    }

    @DisplayName("카테고리 수정 시 카테고리 이름이 30자를 넘는다면 400 에러")
    @Test
    @WithMockUser
    void updateCategoryWithOverLengthCategoryName() throws Exception {
        // Given
        UpdateCategoryReq req = UpdateCategoryReq.builder()
                .name("asdfkhlasdkvasdlkjvakldsvaklsdhvaskldvklasdhvaklsdvhalskdvaksdhl")
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/categories/{categoryId}", 1)
                                .content(objectMapper.writeValueAsString(req))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("카테고리명은 최대 30 글자입니다."));

    }

    @DisplayName("카테고리 삭제")
    @Test
    @WithMockUser
    void deleteCategory() throws Exception {
        // Given

        // When / Then
        mockMvc.perform(
                        delete("/api/categories/{categoryId}", 1)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

    }
}