package com.maruhxn.lossion.domain.favorite.api;

import com.maruhxn.lossion.util.ControllerTestSupport;
import com.maruhxn.lossion.util.CustomWithUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Controller] - FavoriteController")
class FavoriteControllerTest extends ControllerTestSupport {

    @DisplayName("게시글에 좋아요를 남긴다.")
    @Test
    @CustomWithUserDetails
    void topicFavorite() throws Exception {
        mockMvc.perform(
                        patch("/api/favorites/topics/{topicId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("게시글의 좋아요 여부를 확인한다.")
    @Test
    @CustomWithUserDetails
    void checkTopicFavorite() throws Exception {
        mockMvc.perform(
                        get("/api/favorites/topics/{topicId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("댓글에 좋아요를 남긴다.")
    @Test
    @CustomWithUserDetails
    void commentFavorite() throws Exception {
        mockMvc.perform(
                        patch("/api/favorites/comments/{commentId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("댓글의 좋아요 여부를 확인한다.")
    @Test
    @CustomWithUserDetails
    void checkCommentFavorite() throws Exception {
        mockMvc.perform(
                        get("/api/favorites/comments/{commentId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }
}