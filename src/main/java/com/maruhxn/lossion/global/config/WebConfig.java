package com.maruhxn.lossion.global.config;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.global.interceptor.CommentAuthorCheckInterceptor;
import com.maruhxn.lossion.global.interceptor.TopicAuthorCheckInterceptor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(topicAuthorCheckInterceptor())
                .order(1)
                .addPathPatterns("/api/topics/{topicId}, /api/topics/{topicId}/images/{imageId}");
        registry.addInterceptor(commentAuthorCheckInterceptor())
                .order(2)
                .addPathPatterns("/api/topics/{topicId}/comments/{commentId}");
    }

    @Bean
    public TopicAuthorCheckInterceptor topicAuthorCheckInterceptor() {
        return new TopicAuthorCheckInterceptor(topicRepository);
    }

    @Bean
    public CommentAuthorCheckInterceptor commentAuthorCheckInterceptor() {
        return new CommentAuthorCheckInterceptor(commentRepository);
    }

    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory((em));
    }
}
