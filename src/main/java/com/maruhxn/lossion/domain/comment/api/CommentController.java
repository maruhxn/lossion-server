package com.maruhxn.lossion.domain.comment.api;

import com.maruhxn.lossion.domain.comment.application.CommentService;
import com.maruhxn.lossion.domain.comment.dto.request.CreateCommentReq;
import com.maruhxn.lossion.domain.comment.dto.request.UpdateCommentReq;
import com.maruhxn.lossion.domain.comment.dto.response.CommentItem;
import com.maruhxn.lossion.global.auth.dto.CustomUserDetails;
import com.maruhxn.lossion.global.common.dto.BaseResponse;
import com.maruhxn.lossion.global.common.dto.DataResponse;
import com.maruhxn.lossion.global.common.dto.PageItem;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/topics/{topicId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<BaseResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long topicId,
            @RequestBody @Valid CreateCommentReq req
    ) {
        String groupId = String.valueOf(UUID.randomUUID());
        commentService.createComment(userDetails.getMember(), topicId, req, groupId);
        return new ResponseEntity<>(new BaseResponse("댓글 생성 성공"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<DataResponse<PageItem>> getTopLevelComments(
            @PathVariable Long topicId,
            Pageable pageable
    ) {
        PageItem result = commentService.getTopLevelComments(topicId, pageable);
        return ResponseEntity.ok(DataResponse.of("댓글 조회 성공", result));
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateComment(
            @PathVariable Long topicId,
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentReq req
    ) {
        commentService.updateComment(topicId, commentId, req);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long topicId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(topicId, commentId);
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<DataResponse<List<CommentItem>>> getReplies(
            @PathVariable Long topicId,
            @PathVariable String groupId
    ) {
        List<CommentItem> replies = commentService.getRepliesByGroupId(topicId, groupId);
        return ResponseEntity.ok(DataResponse.of("답글 조회 성공", replies));
    }
}
