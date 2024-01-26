package com.maruhxn.lossion.domain.topic.api;

import com.maruhxn.lossion.domain.topic.application.TopicService;
import com.maruhxn.lossion.domain.topic.dto.request.CreateTopicReq;
import com.maruhxn.lossion.domain.topic.dto.request.TopicSearchCond;
import com.maruhxn.lossion.domain.topic.dto.request.UpdateTopicReq;
import com.maruhxn.lossion.domain.topic.dto.request.VoteRequest;
import com.maruhxn.lossion.domain.topic.dto.response.MyTopicItem;
import com.maruhxn.lossion.domain.topic.dto.response.TopicDetailItem;
import com.maruhxn.lossion.domain.topic.dto.response.TopicItem;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RequestMapping("/api/topics")
@RestController
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<DataResponse<PageItem<TopicItem>>> getTopicsByQuery(
            @ModelAttribute @Valid TopicSearchCond cond,
            Pageable pageable
    ) {

        PageItem result = topicService.getTopics(cond, pageable);
        return ResponseEntity.ok(DataResponse.of("주제 리스트 조회 성공", result));
    }

    @PostMapping
    @PreAuthorize("@authChecker.isVerified()")
    public ResponseEntity<BaseResponse> createTopic(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute @Valid CreateTopicReq req
    ) {
        topicService.createTopic(userDetails.getMember(), req, LocalDateTime.now());
        return new ResponseEntity<>(new BaseResponse("주제 생성 성공"), HttpStatus.CREATED);
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<DataResponse<TopicDetailItem>> getTopicDetail(@PathVariable Long topicId) {
        TopicDetailItem result = topicService.getTopicDetail(topicId);
        return ResponseEntity.ok(DataResponse.of("주제 조회 성공", result));
    }

    @PatchMapping("/{topicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTopic(
            @PathVariable Long topicId,
            @ModelAttribute @Valid UpdateTopicReq req
    ) {
        topicService.updateTopic(topicId, req);
    }

    @PatchMapping("/{topicId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeTopic(
            @PathVariable Long topicId
    ) {
        topicService.updateCloseStatus(topicId, LocalDateTime.now());
    }

    @DeleteMapping("/{topicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTopic(
            @PathVariable Long topicId
    ) {
        topicService.deleteTopic(topicId);
    }

    @DeleteMapping("/{topicId}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTopicImage(
            @PathVariable Long imageId
    ) {
        topicService.deleteOneTopicImage(imageId);
    }

    @PatchMapping("/{topicId}/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authChecker.isVerified()")
    public void vote(
            @PathVariable Long topicId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid VoteRequest req
    ) {
        topicService.vote(topicId, userDetails.getId(), req);
    }

    @GetMapping("/my")
    @PreAuthorize("@authChecker.isVerified()")
    public ResponseEntity<DataResponse<PageItem<MyTopicItem>>> getMyTopics(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {

        PageItem result = topicService.getMyTopics(userDetails.getId(), pageable);
        return ResponseEntity.ok(DataResponse.of("내가 작성한 주제 리스트 조회 성공", result));
    }
}
