package com.maruhxn.lossion.domain.favorite.api;

import com.maruhxn.lossion.domain.favorite.application.FavoriteService;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PatchMapping("/topics/{topicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void topicFavorite(
            @PathVariable Long topicId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        favoriteService.topicFavorite(topicId, userDetails.getMember());
    }

    @GetMapping("/topics/{topicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkTopicFavorite(
            @PathVariable Long topicId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        favoriteService.checkTopicFavorite(topicId, userDetails.getMember());
    }

    @PatchMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void commentFavorite(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        favoriteService.commentFavorite(commentId, userDetails.getMember());
    }

    @GetMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkCommentFavorite(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        favoriteService.checkCommentFavorite(commentId, userDetails.getMember());
    }
}
