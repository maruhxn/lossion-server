package com.maruhxn.lossion.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maruhxn.lossion.domain.comment.api.CommentController;
import com.maruhxn.lossion.domain.comment.application.CommentService;
import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.favorite.api.FavoriteController;
import com.maruhxn.lossion.domain.favorite.application.FavoriteService;
import com.maruhxn.lossion.domain.topic.api.CategoryController;
import com.maruhxn.lossion.domain.topic.api.TopicController;
import com.maruhxn.lossion.domain.topic.application.CategoryService;
import com.maruhxn.lossion.domain.topic.application.TopicService;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.global.config.SecurityConfig;
import com.maruhxn.lossion.global.config.WebConfig;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(
        controllers = {
                CommentController.class,
                FavoriteController.class,
                CategoryController.class,
                TopicController.class
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
        }
)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected FavoriteService favoriteService;

    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected TopicService topicService;

    @MockBean
    protected TopicRepository topicRepository;

    @MockBean
    protected CommentRepository commentRepository;

    @MockBean
    protected EntityManager entityManager;
}
