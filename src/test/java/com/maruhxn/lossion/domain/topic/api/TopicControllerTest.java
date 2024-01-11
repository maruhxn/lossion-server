package com.maruhxn.lossion.domain.topic.api;

import com.maruhxn.lossion.domain.topic.dto.request.VoteRequest;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.util.CustomWithUserDetails;
import com.maruhxn.lossion.util.TestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static com.maruhxn.lossion.domain.topic.domain.VoteType.FIRST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("컨트롤러 - TopicController")
class TopicControllerTest extends TestSupport {

    @Test
    @CustomWithUserDetails
    @DisplayName("투표에 성공할 경우 204 응답을 반환한다.")
    void vote() throws Exception {
        // Given
        VoteRequest request = VoteRequest.builder()
                .voteAt(topic.getClosedAt().minusDays(1))
                .voteType(FIRST)
                .build();

        // When / Then
        mvc.perform(
                        patch("/api/topics/{topicId}/vote", topic.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isNoContent());

    }

    @Test
    @CustomWithUserDetails
    @DisplayName("종료된 토론에 투표할 경우 400 응답을 반환한다.")
    void voteOnOverTime() throws Exception {
        // Given
        VoteRequest request = VoteRequest.builder()
                .voteAt(topic.getClosedAt().plusDays(1))
                .voteType(FIRST)
                .build();

        // When / Then
        mvc.perform(
                        patch("/api/topics/{topicId}/vote", topic.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.ALREADY_CLOSED.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_CLOSED.getMessage()))
                .andExpect(jsonPath("$.errors").isArray());
    }

}