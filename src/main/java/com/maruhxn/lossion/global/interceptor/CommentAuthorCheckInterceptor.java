package com.maruhxn.lossion.global.interceptor;

import com.maruhxn.lossion.domain.comment.dao.CommentRepository;
import com.maruhxn.lossion.domain.comment.domain.Comment;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.global.error.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

import static com.maruhxn.lossion.domain.member.domain.Role.ROLE_ADMIN;

@RequiredArgsConstructor
public class CommentAuthorCheckInterceptor implements HandlerInterceptor {

    private final CommentRepository commentRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String method = request.getMethod();

        if (HttpMethod.GET.matches(method)) return true;

        if (HttpMethod.PATCH.matches(method) || HttpMethod.DELETE.matches(method)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails loginMember = (CustomUserDetails) authentication.getPrincipal();

            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
                    HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            Long commentId = Long.valueOf(String.valueOf(pathVariables.get("commentId")));
            Comment findComment = commentRepository.findById(commentId).orElseThrow(
                    () -> new EntityNotFoundException(ErrorCode.NOT_FOUND_COMMENT));

            if (!loginMember.getAuthorities().contains(ROLE_ADMIN)
                    && !loginMember.getId().equals(findComment.getAuthor().getId())) {
                throw new ForbiddenException(ErrorCode.FORBIDDEN);
            }

            return true;
        }

        throw new BadRequestException(ErrorCode.BAD_REQUEST);
    }
}
