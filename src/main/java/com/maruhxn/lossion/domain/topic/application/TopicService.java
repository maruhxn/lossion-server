package com.maruhxn.lossion.domain.topic.application;

import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.*;
import com.maruhxn.lossion.domain.topic.domain.Category;
import com.maruhxn.lossion.domain.topic.domain.Topic;
import com.maruhxn.lossion.domain.topic.domain.TopicImage;
import com.maruhxn.lossion.domain.topic.domain.Vote;
import com.maruhxn.lossion.domain.topic.dto.request.CreateTopicReq;
import com.maruhxn.lossion.domain.topic.dto.request.TopicSearchCond;
import com.maruhxn.lossion.domain.topic.dto.request.UpdateTopicReq;
import com.maruhxn.lossion.domain.topic.dto.request.VoteRequest;
import com.maruhxn.lossion.domain.topic.dto.response.TopicDetailItem;
import com.maruhxn.lossion.domain.topic.dto.response.TopicItem;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.common.dto.PageItem;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.infra.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TopicService {

    private final MemberRepository memberRepository;
    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;
    private final TopicImageRepository topicImageRepository;
    private final TopicQueryRepository topicQueryRepository;
    private final VoteRepository voteRepository;

    private final FileService fileService;

    public PageItem getTopics(@Valid TopicSearchCond cond, Pageable pageable) {
        Page<TopicItem> result = topicQueryRepository.findAllByConditions(cond, pageable);
        return PageItem.from(result);
    }

    @Transactional
    public void createTopic(JwtMemberInfo jwtMemberInfo, CreateTopicReq req) {
        Member author = Member.from(jwtMemberInfo); // 영속성 관리 안 되는데 괜찮나?
        Category findCategory = findCategoryByIdOrThrow(req.getCategoryId());

        List<TopicImage> topicImages = storeTopicImageList(req.getImages());

        Topic topic = Topic.of(author, findCategory, topicImages, req);

        topicRepository.save(topic);
    }

    private Category findCategoryByIdOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    private List<TopicImage> storeTopicImageList(List<MultipartFile> images) {
        if (images != null && !images.isEmpty()) {
            return fileService.storeFiles(images);
        }
        return new ArrayList<>();
    }

    @Transactional
    public TopicDetailItem getTopicDetail(Long topicId) {
        Topic findTopic = topicRepository.findTopicWithMemberAndImagesAndCategoryById(topicId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOPIC));
        findTopic.addViewCount();
        return TopicDetailItem.from(findTopic);
    }

    private Topic findTopicByIdOrThrow(Long postId) {
        return topicRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOPIC));
    }

    @Transactional
    public void updateTopic(Long topicId, UpdateTopicReq req) {
        Topic findTopic = findTopicByIdOrThrow(topicId);

        if (req.getCategoryId() != null) {
            Category findCategory = findCategoryByIdOrThrow(req.getCategoryId());
            findTopic.changeCategory(findCategory);
        }

        List<TopicImage> topicImages = storeTopicImageList(req.getImages());

        findTopic.updateTopic(req, topicImages);
    }

    @Transactional
    public void deleteTopic(Long topicId) {
        Topic findTopic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOPIC));
        topicRepository.delete(findTopic);
        findTopic.getImages()
                .forEach(topicImage -> fileService.deleteFile(topicImage.getStoredName()));
    }

    @Transactional
    public void deleteOneTopicImage(Long imageId) {
        TopicImage findTopicImage = topicImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOPIC_IMAGE));

        topicImageRepository.delete(findTopicImage);

        fileService.deleteFile(findTopicImage.getStoredName());
    }

    @Transactional
    public void closeTopic(Long topicId) {
        Topic findTopic = findTopicByIdOrThrow(topicId);
        findTopic.updateCloseStatus(LocalDateTime.now());
    }

    @Transactional
    public Vote vote(Long topicId, Long memberId, VoteRequest req) {

        Topic findTopic = findTopicByIdOrThrow(topicId);
        validateVoteTime(findTopic.getClosedAt(), req.getVoteAt());

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        Optional<Vote> optionalVote = voteRepository.findByTopicIdAndVoter_Id(topicId, findMember.getId());
        boolean isNewVote = optionalVote.isEmpty();

        if (isNewVote) {
            Vote vote = Vote.of(findMember, findTopic, req);
            return voteRepository.save(vote);
        } else {
            Vote findVote = optionalVote.get();
            findVote.updateVoteType(req);
            return findVote;
        }
    }

    private static void validateVoteTime(LocalDateTime closedAt, LocalDateTime voteAt) {

        if (voteAt.isAfter(closedAt)) {
            throw new BadRequestException(ErrorCode.ALREADY_CLOSED);
        }
    }
}
