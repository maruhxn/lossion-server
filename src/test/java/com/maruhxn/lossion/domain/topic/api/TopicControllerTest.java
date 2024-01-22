package com.maruhxn.lossion.domain.topic.api;

import com.maruhxn.lossion.domain.topic.domain.VoteType;
import com.maruhxn.lossion.domain.topic.dto.request.TopicSearchCond;
import com.maruhxn.lossion.domain.topic.dto.request.VoteRequest;
import com.maruhxn.lossion.domain.topic.dto.response.MyTopicItem;
import com.maruhxn.lossion.domain.topic.dto.response.TopicDetailItem;
import com.maruhxn.lossion.domain.topic.dto.response.TopicItem;
import com.maruhxn.lossion.global.common.dto.PageItem;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.util.ControllerTestSupport;
import com.maruhxn.lossion.util.CustomWithUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Controller] - TopicController")
class TopicControllerTest extends ControllerTestSupport {

    @DisplayName("주제 리스트를 페이징 조회한다.")
    @Test
    @WithMockUser
    void getTopicsByQuery() throws Exception {
        // Given
        PageItem<TopicItem> result = PageItem.<TopicItem>builder()
                .results(List.of())
                .isFirst(true)
                .isLast(true)
                .isEmpty(false)
                .totalPage(1)
                .totalElements(1L)
                .build();

        given(topicService.getTopics(any(TopicSearchCond.class), any(Pageable.class)))
                .willReturn(result);
        // When / Then
        mockMvc.perform(
                        get("/api/topics")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("주제 리스트 조회 성공"))
                .andExpect(jsonPath("data").exists())
                .andDo(print());
    }

    @DisplayName("주제 리스트를 조회 시 '작성자' 조건은 10글자를 넘길 수 없다.")
    @Test
    @WithMockUser
    void getTopicsByQueryWithOverLengthAuthorName() throws Exception {
        // Given
        // When / Then
        mockMvc.perform(
                        get("/api/topics")
                                .queryParam("author", "anyLongName")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("유저명 검색은 최대 10글자입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("주제를 생성한다.")
    @CustomWithUserDetails
    void createTopic() throws Exception {
        // Given
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();
        // When / Then
        mockMvc.perform(
                        multipart("/api/topics")
                                .param("title", "test")
                                .param("description", "test")
                                .param("firstChoice", "firstChoice")
                                .param("secondChoice", "secondChoice")
                                .param("closedAt", closedAtStr)
                                .param("categoryId", "1")
                                .with(csrf())

                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("주제 생성 성공"));
    }

    @Test
    @DisplayName("이미지와 함께 주제를 생성한다.")
    @CustomWithUserDetails
    void createTopicWithImages() throws Exception {
        // Given
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();
        MockMultipartFile image1 = getMockMultipartFile();
        MockMultipartFile image2 = getMockMultipartFile();

        // When / Then
        mockMvc.perform(
                        multipart("/api/topics")
                                .file(image1).file(image2)
                                .param("title", "test")
                                .param("description", "test")
                                .param("firstChoice", "firstChoice")
                                .param("secondChoice", "secondChoice")
                                .param("closedAt", closedAtStr)
                                .param("categoryId", "1")
                                .with(csrf())

                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("주제 생성 성공"));
    }

    @Test
    @DisplayName("주제를 생성 시 제목을 전달하지 않으면 400 에러를 반환한다.")
    @WithMockUser
    void createTopicWithoutTitle() throws Exception {
        // Given
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();
        // When / Then
        mockMvc.perform(
                        multipart("/api/topics")
                                .param("description", "test")
                                .param("firstChoice", "firstChoice")
                                .param("secondChoice", "secondChoice")
                                .param("closedAt", closedAtStr)
                                .param("categoryId", "1")
                                .with(csrf())

                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("제목을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("주제를 생성 시 1글자 제목을 전달하면 400 에러를 반환한다.")
    @WithMockUser
    void createTopicWithShortTitle() throws Exception {
        // Given
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();
        // When / Then
        mockMvc.perform(
                        multipart("/api/topics")
                                .param("title", "1")
                                .param("description", "test")
                                .param("firstChoice", "firstChoice")
                                .param("secondChoice", "secondChoice")
                                .param("closedAt", closedAtStr)
                                .param("categoryId", "1")
                                .with(csrf())

                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("제목은 2 ~ 255 글자입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("주제를 생성 시 내용을 전달하지 않으면 400 에러를 반환한다.")
    @WithMockUser
    void createTopicWithoutDescription() throws Exception {
        // Given
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();
        // When / Then
        mockMvc.perform(
                        multipart("/api/topics")
                                .param("title", "test")
                                .param("firstChoice", "firstChoice")
                                .param("secondChoice", "secondChoice")
                                .param("closedAt", closedAtStr)
                                .param("categoryId", "1")
                                .with(csrf())

                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("내용을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("주제를 생성 시 선택지를 전달하지 않으면 400 에러를 반환한다.")
    @WithMockUser
    void createTopicWithoutChoice() throws Exception {
        // Given
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();
        // When / Then
        mockMvc.perform(
                        multipart("/api/topics")
                                .param("title", "test")
                                .param("description", "test")
                                .param("closedAt", closedAtStr)
                                .param("categoryId", "1")
                                .with(csrf())

                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors.size()").value(2))
                .andDo(print());
    }

    @Test
    @DisplayName("주제를 생성 시 토론 종료 시각을 전달하지 않으면 400 에러를 반환한다.")
    @WithMockUser
    void createTopicWithoutClosedAt() throws Exception {
        // Given
        // When / Then
        mockMvc.perform(
                        multipart("/api/topics")
                                .param("title", "test")
                                .param("description", "test")
                                .param("firstChoice", "firstChoice")
                                .param("secondChoice", "secondChoice")
                                .param("categoryId", "1")
                                .with(csrf())

                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("토론 종료 시각을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("주제를 생성 시 카테고리 아이디를 전달하지 않으면 400 에러를 반환한다.")
    @WithMockUser
    void createTopicWithoutCategoryId() throws Exception {
        // Given
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();
        // When / Then
        mockMvc.perform(
                        multipart("/api/topics")
                                .param("title", "test")
                                .param("description", "test")
                                .param("firstChoice", "firstChoice")
                                .param("secondChoice", "secondChoice")
                                .param("closedAt", closedAtStr)
                                .with(csrf())

                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("카테고리를 선택해주세요."))
                .andDo(print());
    }

    @DisplayName("주제를 상세 조회할 수 있다.")
    @Test
    @WithMockUser
    void getTopicDetail() throws Exception {
        // Given
        TopicDetailItem result = TopicDetailItem.builder()
                .build();

        given(topicService.getTopicDetail(anyLong()))
                .willReturn(result);

        // When / Then
        mockMvc.perform(
                        get("/api/topics/{topicId}", 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("주제 조회 성공"))
                .andExpect(jsonPath("data").isNotEmpty())
                .andDo(print());
    }

    @DisplayName("주제를 상세 조회 시 올바르지 않은 topicId를 전달할 경우 400 에러를 반환한다.")
    @Test
    @WithMockUser
    void getTopicDetailWithInvalidPathVariable() throws Exception {
        mockMvc.perform(
                        get("/api/topics/{topicId}", "hack")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.PATH_VAR_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.PATH_VAR_ERROR.getMessage()))
                .andDo(print());
    }

    @DisplayName("주제의 제목을 수정할 수 있다.")
    @Test
    @WithMockUser
    void updateTopicWithTitle() throws Exception {
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/api/topics/{topicId}", 1)
                                .param("title", "test")
                                .with(csrf())

                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("주제를 토론 종료 시각을 수정하고 이미지를 추가할 수 있다.")
    @Test
    @WithMockUser
    void updateTopicWithClosedAtAndImage() throws Exception {
        // Given
        String closedAtStr = LocalDateTime.of(2024, 1, 17, 10, 0).toString();
        MockMultipartFile image1 = getMockMultipartFile();

        // When / Then
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/api/topics/{topicId}", 1)
                                .file(image1)
                                .param("closedAt", closedAtStr)
                                .with(csrf())

                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("토론을 종료할 수 있다.")
    @Test
    @WithMockUser
    void closeTopic() throws Exception {
        mockMvc.perform(
                        patch("/api/topics/{topicId}/status", 1)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("토론 종료 시 올바르지 않은 topicId를 전달할 경우 400 에러를 반환한다.")
    @Test
    @WithMockUser
    void closeTopicWithInvalidPathVariable() throws Exception {
        mockMvc.perform(
                        patch("/api/topics/{topicId}/status", "hack")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.PATH_VAR_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.PATH_VAR_ERROR.getMessage()))
                .andDo(print());
    }

    @DisplayName("주제를 삭제할 수 있다.")
    @Test
    @WithMockUser
    void deleteTopic() throws Exception {
        mockMvc.perform(
                        delete("/api/topics/{topicId}", 1)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("주제 삭제 시 올바르지 않은 topicId를 전달할 경우 400 에러를 반환한다.")
    @Test
    @WithMockUser
    void deleteTopicWithInvalidPathVariable() throws Exception {
        mockMvc.perform(
                        delete("/api/topics/{topicId}", "hack")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.PATH_VAR_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.PATH_VAR_ERROR.getMessage()))
                .andDo(print());
    }

    @DisplayName("이미지를 삭제할 수 있다.")
    @Test
    @WithMockUser
    void deleteTopicImage() throws Exception {
        mockMvc.perform(
                        delete("/api/topics/{topicId}/images/{imageId}", 1, 1)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("이미지 삭제 시 올바르지 않은 topicId를 전달할 경우 400 에러를 반환한다.")
    @Test
    @WithMockUser
    void deleteTopicImageWithInvalidPathVariable1() throws Exception {
        mockMvc.perform(
                        delete("/api/topics/{topicId}/images/{imageId}", 1, "hack")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.PATH_VAR_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.PATH_VAR_ERROR.getMessage()))
                .andDo(print());
    }

    private MockMultipartFile getMockMultipartFile() throws IOException {
        final String originalFileName = "defaultProfileImage.jfif";
        final String filePath = "src/test/resources/static/img/" + originalFileName;

        return new MockMultipartFile(
                "images", //name
                originalFileName,
                "image/jpeg",
                new FileInputStream(filePath)
        );
    }


    @Test
    @CustomWithUserDetails
    @DisplayName("투표에 성공할 경우 204 응답을 반환한다.")
    void vote() throws Exception {
        // Given
        VoteRequest request = VoteRequest.builder()
                .voteAt(LocalDateTime.of(2024, 1, 17, 10, 0))
                .voteType(VoteType.FIRST)
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/topics/{topicId}/vote", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

    }

    @Test
    @CustomWithUserDetails
    @DisplayName("투표 시 투표 정보가 비어있을 경우 400 에러를 반환한다.")
    void voteWithoutVoteType() throws Exception {
        // Given
        VoteRequest request = VoteRequest.builder()
                .voteAt(LocalDateTime.of(2024, 1, 17, 10, 0))
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/topics/{topicId}/vote", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("투표 정보는 비어있을 수 없습니다."))
                .andDo(print());

    }

    @Test
    @CustomWithUserDetails
    @DisplayName("투표 시 투표 시각이 비어있을 경우 400 에러를 반환한다.")
    void voteWithoutVoteAt() throws Exception {
        // Given
        VoteRequest request = VoteRequest.builder()
                .voteType(VoteType.FIRST)
                .build();

        // When / Then
        mockMvc.perform(
                        patch("/api/topics/{topicId}/vote", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].reason").value("투표 시각은 비어있을 수 없습니다."))
                .andDo(print());

    }

    @Test
    @CustomWithUserDetails
    @DisplayName("내가 작성한 주제 리스트 조회에 성공할 경우 200 응답과 함께 내가 작성한 주제 리스트의 페이징 정보를 반환한다.")
    void getMyTopics() throws Exception {

        PageItem<MyTopicItem> result = PageItem.<MyTopicItem>builder()
                .results(List.of())
                .isFirst(true)
                .isLast(true)
                .isEmpty(false)
                .totalPage(1)
                .totalElements(1L)
                .build();

        given(topicService.getMyTopics(anyLong(), any(Pageable.class)))
                .willReturn(result);

        mockMvc.perform(
                        get("/api/topics/my")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("내가 작성한 주제 리스트 조회 성공"))
                .andExpect(jsonPath("$.data.isFirst").value(true))
                .andExpect(jsonPath("$.data.isLast").value(true))
                .andExpect(jsonPath("$.data.isEmpty").value(false))
                .andExpect(jsonPath("$.data.totalPage").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1L))
                .andExpect(jsonPath("$.data.results").isArray());

    }
}