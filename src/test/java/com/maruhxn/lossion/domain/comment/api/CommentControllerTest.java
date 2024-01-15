package com.maruhxn.lossion.domain.comment.api;

import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.domain.comment.dto.request.CreateCommentReq;
import com.maruhxn.lossion.domain.comment.dto.request.UpdateCommentReq;
import com.maruhxn.lossion.global.common.dto.PageItem;
import com.maruhxn.lossion.util.ControllerTestSupport;
import com.maruhxn.lossion.util.CustomWithUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[컨트롤러] - CommentController")
class CommentControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("댓글을 작성한다.")
    @CustomWithUserDetails
    void createComment() throws Exception {
        // Given
        CreateCommentReq req = CreateCommentReq.builder()
                .text("test")
                .build();

        // When / Then
        mockMvc.perform(
                        post("/api/topics/{topicId}/comments", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("댓글 생성 성공"));
    }

    @Test
    @DisplayName("댓글 작성 시 내용을 넘겨주지 않은 경우, 400 에러가 발생한다.")
    @CustomWithUserDetails
    void createCommentWithoutText() throws Exception {
        // Given
        CreateCommentReq req = CreateCommentReq.builder()
                .build();

        // When / Then
        mockMvc.perform(
                        post("/api/topics/{topicId}/comments", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("message").value("올바르지 않은 입력입니다."))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("내용은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("댓글을 페이징 조회한다.")
    @CustomWithUserDetails
    void getCommentList() throws Exception {
        // Given
        PageItem<Comment> result = PageItem.<Comment>builder()
                .results(List.of())
                .isFirst(true)
                .isLast(true)
                .isEmpty(true)
                .totalPage(0)
                .totalElements(0L)
                .build();
        when(commentService.getTopLevelComments(anyLong(), any())).thenReturn(result);
        // When / Then
        mockMvc.perform(
                        get("/api/topics/{topicId}/comments", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("댓글 조회 성공"))
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("data.results").isArray());
    }

    @Test
    @DisplayName("댓글을 수정한다.")
    @CustomWithUserDetails
    void updateComment() throws Exception {
        // Given
        UpdateCommentReq req = UpdateCommentReq.builder()
                .text("test")
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/topics/{topicId}/comments/{commentId}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 수정 시 내용을 넘겨주지 않은 경우, 400 에러가 발생한다.")
    @CustomWithUserDetails
    void updateCommentWithoutText() throws Exception {
        // Given
        UpdateCommentReq req = UpdateCommentReq.builder()
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/topics/{topicId}/comments/{commentId}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("message").value("올바르지 않은 입력입니다."))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("내용은 비어있을 수 없습니다."));
    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    @CustomWithUserDetails
    void deleteComment() throws Exception {
        mockMvc.perform(
                        delete("/api/topics/{topicId}/comments/{commentId}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("답글을 조회한다.")
    @CustomWithUserDetails
    void getReplies() throws Exception {
        // Given
        when(commentService.getRepliesByGroupId(anyLong(), anyString())).thenReturn(List.of());
        // When / Then
        mockMvc.perform(
                        get("/api/topics/{topicId}/comments/groups/{groupId}", 1, "groupId")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("답글 조회 성공"))
                .andExpect(jsonPath("data").isArray());
    }
}