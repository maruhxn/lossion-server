package com.maruhxn.lossion.domain.topic.application;

import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.topic.dao.CategoryRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicImageRepository;
import com.maruhxn.lossion.domain.topic.dao.TopicRepository;
import com.maruhxn.lossion.domain.topic.dao.VoteRepository;
import com.maruhxn.lossion.domain.topic.domain.*;
import com.maruhxn.lossion.domain.topic.dto.request.CreateTopicReq;
import com.maruhxn.lossion.domain.topic.dto.request.TopicSearchCond;
import com.maruhxn.lossion.domain.topic.dto.request.UpdateTopicReq;
import com.maruhxn.lossion.domain.topic.dto.request.VoteRequest;
import com.maruhxn.lossion.global.common.dto.PageItem;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.util.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("[Service] - TopicService")
class TopicServiceTest extends IntegrationTestSupport {

    @Autowired
    private TopicService topicService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private TopicImageRepository topicImageRepository;

    @DisplayName("주제 리스트 조회 시 검색 조건을 전달하지 않을 경우, 최신 10개의 주제를 페이징 조회힌다.")
    @Test
    void getTopics() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic1 = createTopic("title", "test", closedAt, member, category);
        Topic topic2 = createTopic("title", "test", closedAt, member, category);
        Topic topic3 = createTopic("title", "test", closedAt, member, category);
        Topic topic4 = createTopic("title", "test", closedAt, member, category);
        Topic topic5 = createTopic("title", "test", closedAt, member, category);
        Topic topic6 = createTopic("title", "test", closedAt, member, category);
        Topic topic7 = createTopic("title", "test", closedAt, member, category);
        Topic topic8 = createTopic("title", "test", closedAt, member, category);
        Topic topic9 = createTopic("title", "test", closedAt, member, category);
        Topic topic10 = createTopic("title", "test", closedAt, member, category);
        Topic topic11 = createTopic("title", "test", closedAt, member, category);

        topicRepository.saveAll(List.of(topic1, topic2, topic3, topic4, topic5, topic6, topic7, topic8, topic9, topic10, topic11));

//        List<Topic> resultTopics = List.of(topic11, topic10, topic9, topic8, topic7, topic6, topic5, topic4, topic3, topic2);
//        List<TopicItem> results = resultTopics.stream()
//                .map(TopicItem::from)
//                .toList();

        TopicSearchCond cond = TopicSearchCond.builder()
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        // When
        PageItem topics = topicService.getTopics(cond, pageRequest);

        // Then
        // TODO: 더 명확하도록 개선 필요
        assertThat(topics)
                .extracting("results", "isFirst", "isLast", "isEmpty", "totalPage", "totalElements")
                .contains(topics.getResults(), true, false, false, 2, 11L);
    }

    @DisplayName("주제 리스트 조회 시 '제목' 조건을 전달한다면, 전달받은 제목 키워드를 포함하는 최신 10개의 주제를 페이징 조회힌다.")
    @Test
    void getTopicsWithTitleCond() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic1 = createTopic("title1", "test", closedAt, member, category);
        Topic topic2 = createTopic("title1", "test", closedAt, member, category);
        Topic topic3 = createTopic("title1", "test", closedAt, member, category);
        Topic topic4 = createTopic("title1", "test", closedAt, member, category);
        Topic topic5 = createTopic("title1", "test", closedAt, member, category);
        Topic topic6 = createTopic("title1", "test", closedAt, member, category);
        Topic topic7 = createTopic("title1", "test", closedAt, member, category);
        Topic topic8 = createTopic("title1", "test", closedAt, member, category);
        Topic topic9 = createTopic("title2", "test", closedAt, member, category);
        Topic topic10 = createTopic("title2", "test", closedAt, member, category);
        Topic topic11 = createTopic("title2", "test", closedAt, member, category);

        topicRepository.saveAll(List.of(topic1, topic2, topic3, topic4, topic5, topic6, topic7, topic8, topic9, topic10, topic11));

        TopicSearchCond cond = TopicSearchCond.builder()
                .title("title1")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        // When
        PageItem topics = topicService.getTopics(cond, pageRequest);

        // Then
        assertThat(topics)
                .extracting("isFirst", "isLast", "isEmpty", "totalPage", "totalElements")
                .contains(true, true, false, 1, 8L);
        assertThat(topics.getResults()).hasSize(8);
    }

    @DisplayName("주제 리스트 조회 시 '내용' 조건을 전달한다면, 전달받은 내용 키워드를 포함하는 최신 10개의 주제를 페이징 조회힌다.")
    @Test
    void getTopicsWithDescriptionCond() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic1 = createTopic("title1", "test1", closedAt, member, category);
        Topic topic2 = createTopic("title1", "test1", closedAt, member, category);
        Topic topic3 = createTopic("title1", "test1", closedAt, member, category);
        Topic topic4 = createTopic("title1", "test1", closedAt, member, category);
        Topic topic5 = createTopic("title1", "test1", closedAt, member, category);
        Topic topic6 = createTopic("title1", "test1", closedAt, member, category);
        Topic topic7 = createTopic("title1", "test2", closedAt, member, category);
        Topic topic8 = createTopic("title1", "test2", closedAt, member, category);
        Topic topic9 = createTopic("title2", "test2", closedAt, member, category);
        Topic topic10 = createTopic("title2", "test2", closedAt, member, category);
        Topic topic11 = createTopic("title2", "test2", closedAt, member, category);

        topicRepository.saveAll(List.of(topic1, topic2, topic3, topic4, topic5, topic6, topic7, topic8, topic9, topic10, topic11));

        TopicSearchCond cond = TopicSearchCond.builder()
                .description("test2")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        // When
        PageItem topics = topicService.getTopics(cond, pageRequest);

        // Then
        assertThat(topics)
                .extracting("isFirst", "isLast", "isEmpty", "totalPage", "totalElements")
                .contains(true, true, false, 1, 5L);
        assertThat(topics.getResults()).hasSize(5);
    }

    @DisplayName("주제 리스트 조회 시 '작성자' 조건을 전달한다면, 전달받은 작성자와 일치하는 최신 10개의 주제를 페이징 조회힌다.")
    @Test
    void getTopicsWithAuthorCond() {
        // Given
        Member member1 = createMember();
        Member member2 = createSubMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic1 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic2 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic3 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic4 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic5 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic6 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic7 = createTopic("title1", "test", closedAt, member2, category);
        Topic topic8 = createTopic("title1", "test", closedAt, member2, category);
        Topic topic9 = createTopic("title2", "test", closedAt, member2, category);
        Topic topic10 = createTopic("title2", "test", closedAt, member2, category);
        Topic topic11 = createTopic("title2", "test", closedAt, member2, category);

        topicRepository.saveAll(List.of(topic1, topic2, topic3, topic4, topic5, topic6, topic7, topic8, topic9, topic10, topic11));

        TopicSearchCond cond = TopicSearchCond.builder()
                .author(member1.getUsername())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        // When
        PageItem topics = topicService.getTopics(cond, pageRequest);

        // Then
        assertThat(topics)
                .extracting("isFirst", "isLast", "isEmpty", "totalPage", "totalElements")
                .contains(true, true, false, 1, 6L);
        assertThat(topics.getResults()).hasSize(6);
    }

    @DisplayName("주제 리스트 조회 시 '제목'과 '작성자' 조건을 함께 전달한다면, 전달받은 제목을 포함하고 작성자가 일치하는 최신 10개의 주제를 페이징 조회힌다.")
    @Test
    void getTopicsWithTitleAndAuthorCond() {
        // Given
        Member member1 = createMember();
        Member member2 = createSubMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic1 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic2 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic3 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic4 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic5 = createTopic("title1", "test", closedAt, member1, category);
        Topic topic6 = createTopic("title1", "test", closedAt, member2, category);
        Topic topic7 = createTopic("title1", "test", closedAt, member2, category);
        Topic topic8 = createTopic("title1", "test", closedAt, member2, category);
        Topic topic9 = createTopic("title2", "test", closedAt, member2, category);
        Topic topic10 = createTopic("title2", "test", closedAt, member2, category);
        Topic topic11 = createTopic("title2", "test", closedAt, member2, category);

        topicRepository.saveAll(List.of(topic1, topic2, topic3, topic4, topic5, topic6, topic7, topic8, topic9, topic10, topic11));

        TopicSearchCond cond = TopicSearchCond.builder()
                .title("title1")
                .author(member1.getUsername())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        // When
        PageItem topics = topicService.getTopics(cond, pageRequest);

        // Then
        assertThat(topics)
                .extracting("isFirst", "isLast", "isEmpty", "totalPage", "totalElements")
                .contains(true, true, false, 1, 5L);
        assertThat(topics.getResults()).hasSize(5);
    }

    @DisplayName("이미지 데이터 없이 주제를 생성한다.")
    @Test
    void createTopicWithoutImages() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        CreateTopicReq req = CreateTopicReq.builder()
                .title("test")
                .description("test")
                .firstChoice("first")
                .secondChoice("second")
                .closedAt(closedAt)
                .categoryId(category.getId())
                .build();

        // When
        Topic topic = topicService.createTopic(member, req, closedAt.minusDays(1));

        // Then
        Topic findTopic = topicRepository.findById(topic.getId()).get();
        assertThat(findTopic).isEqualTo(topic);
    }

    @DisplayName("이미지 데이터를 포함하여 주제를 생성한다.")
    @Test
    void createTopicWithImages() throws IOException {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);

        final String fileName = "defaultProfileImage"; // 파일명
        final String contentType = "jfif"; // 파일타입
        final String filePath = "src/test/resources/static/img/" + fileName + "." + contentType; //파일경로

        MockMultipartFile image1 = new MockMultipartFile(
                "images", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                new FileInputStream(filePath)
        );

        MockMultipartFile image2 = new MockMultipartFile(
                "images", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                new FileInputStream(filePath)
        );

        CreateTopicReq req = CreateTopicReq.builder()
                .title("test")
                .description("test")
                .firstChoice("first")
                .secondChoice("second")
                .closedAt(closedAt)
                .categoryId(category.getId())
                .images(List.of(image1, image2))
                .build();

        given(fileService.storeOneFile(any(MultipartFile.class)))
                .willReturn("storedName");

        // When
        Topic topic = topicService.createTopic(member, req, closedAt.minusDays(1));

        // Then
        Topic findTopic = topicRepository.findById(topic.getId()).get();
        assertThat(findTopic).isEqualTo(topic);
    }

    @DisplayName("주제를 생성 시 제목 정보를 넘겨주지 않으면 에러가 발생한다.")
    @Test
    void createTopicWithoutTitle() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        CreateTopicReq req = CreateTopicReq.builder()
                .description("test")
                .firstChoice("first")
                .secondChoice("second")
                .closedAt(closedAt)
                .categoryId(category.getId())
                .build();

        // When / Then
        assertThatThrownBy(() -> topicService.createTopic(member, req, closedAt.minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 필수입니다.");
    }

    @DisplayName("주제를 생성 시 제목이 1글자이면 에러가 발생한다.")
    @Test
    void createTopicWithLessThan1LengthTitle() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        CreateTopicReq req = CreateTopicReq.builder()
                .title("1")
                .description("test")
                .firstChoice("first")
                .secondChoice("second")
                .closedAt(closedAt)
                .categoryId(category.getId())
                .build();

        // When / Then
        assertThatThrownBy(() -> topicService.createTopic(member, req, closedAt.minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 2글자 이상이어야 합니다.");
    }

    @DisplayName("주제를 생성 시 제목이 255글자 초과이면 에러가 발생한다.")
    @Test
    void createTopicWithOverLengthTitle() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        String overLegnthTitle = "sdkvaslafhskldfhalsdkvklasndlvkasddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddnfklsdahrfklsadjfklsajdfklajklnmewklfmeklasjdklfajskldfjasdklfjasdklfjklqwndvklasdklrjqwekljfaklsdjflkqjweklfajskldvjaskldjrklwejqflkajsdklfajlsdkfjwklafjsdklfjaklsefjklwefjaklsdjvklasjdfkvalsjvklasdjvklasdjvklsjdklva";
        CreateTopicReq req = CreateTopicReq.builder()
                .title(overLegnthTitle)
                .description("test")
                .firstChoice("first")
                .secondChoice("second")
                .closedAt(closedAt)
                .categoryId(category.getId())
                .build();

        // When / Then
        assertThatThrownBy(() -> topicService.createTopic(member, req, closedAt.minusDays(1)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("주제를 수정할 수 있다.")
    @Test
    void updateTopic() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        UpdateTopicReq req = UpdateTopicReq.builder()
                .title("title!!")
                .build();

        // When
        topicService.updateTopic(topic.getId(), req);

        // Then
        Topic findTopic = topicRepository.findById(topic.getId()).get();
        assertThat(findTopic)
                .extracting("title")
                .isEqualTo("title!!");

    }

    @DisplayName("주제를 수정 시 이미지 리스트를 전달하면, 새롭게 추가된다.")
    @Test
    void updateTopicWithImages() throws IOException {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        final String fileName = "defaultProfileImage"; // 파일명
        final String contentType = "jfif"; // 파일타입
        final String filePath = "src/test/resources/static/img/" + fileName + "." + contentType; //파일경로

        MockMultipartFile image1 = new MockMultipartFile(
                "images", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                new FileInputStream(filePath)
        );

        given(fileService.storeOneFile(any()))
                .willReturn("storeFileName");

        UpdateTopicReq req = UpdateTopicReq.builder()
                .title("title!!")
                .images(List.of(image1))
                .build();

        // When
        topicService.updateTopic(topic.getId(), req);

        // Then
        Topic findTopic = topicRepository.findById(topic.getId()).get();
        assertThat(findTopic)
                .extracting("title")
                .isEqualTo("title!!");
        assertThat(findTopic.getImages()).hasSize(1);

    }

    @DisplayName("주제 수정 시, 넘겨 준 topicId에 해당하는 주제가 없으면 에러를 반환한다.")
    @Test
    void updateTopicNotFoundTopic() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        UpdateTopicReq req = UpdateTopicReq.builder()
                .title("title!!")
                .build();

        // When / Then
        assertThatThrownBy(() -> topicService.updateTopic(2L, req))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_TOPIC.getMessage());

    }

    @DisplayName("주제 수정 시, 넘겨 준 categoryId에 해당하는 카테고리가 없으면 에러를 반환한다.")
    @Test
    void updateTopicNotFoundCategory() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        UpdateTopicReq req = UpdateTopicReq.builder()
                .title("title!!")
                .categoryId(2L)
                .build();

        // When / Then
        assertThatThrownBy(() -> topicService.updateTopic(topic.getId(), req))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_CATEGORY.getMessage());

    }

    @DisplayName("주제를 삭제할 수 있고, 삭제 시 하위 이미지는 모두 삭제된다.")
    @Test
    void deleteTopic() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        // When
        topicService.deleteTopic(topic.getId());

        // Then
        assertThat(topicRepository.findById(topic.getId()).isEmpty()).isTrue();
        assertThat(topicImageRepository.findAll()).isEmpty();

    }

    @DisplayName("주제 삭제 시, 넘겨 준 topicId에 해당하는 주제가 없으면 에러를 반환한다.")
    @Test
    void deleteTopicNotFoundTopic() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        // When / Then
        assertThatThrownBy(() -> topicService.deleteTopic(2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_TOPIC.getMessage());

    }

    @DisplayName("이미지를 삭제할 수 있다.")
    @Test
    void deleteOneTopicImage() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        TopicImage topicImage = TopicImage.builder()
                .originalName("originalName")
                .storedName("storedName")
                .build();

        topicImageRepository.save(topicImage);
        topic.addTopicImage(topicImage);
        topicRepository.save(topic);

        // When
        topicService.deleteOneTopicImage(topicImage.getId());

        // Then
        assertThat(topicImageRepository.findById(topicImage.getId())).isEmpty();

    }

    @DisplayName("이미지 삭제 시, 넘겨 준 imageId에 해당하는 이미지가 없으면 에러를 반환한다.")
    @Test
    void deleteOneTopicImageNotFoundTopic() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);

        topicRepository.save(topic);

        // When / Then
        assertThatThrownBy(() -> topicService.deleteOneTopicImage(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_TOPIC_IMAGE.getMessage());

    }

    @DisplayName("투표 종료 시각이 지난 경우 투표를 종료할 수 있다.")
    @Test
    void closeTopic() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        // When
        topicService.updateCloseStatus(topic.getId());

        // Then
        Topic findTopic = topicRepository.findById(topic.getId()).get();
        assertThat(findTopic.getIsClosed()).isTrue();

    }

    @DisplayName("투표 종료 시각이 지난 경우 투표는 종료할 수 없다.")
    @Test
    void closeTopicWhenIsBefore() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 16, 10, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        // When
        topicService.updateCloseStatus(topic.getId());

        // Then
        Topic findTopic = topicRepository.findById(topic.getId()).get();
        assertThat(findTopic.getIsClosed()).isFalse();

    }

    @DisplayName("기존 투표 정보가 없을 때 투표를 하는 경우, 투표 정보는 요청한 투표 정보와 같다.")
    @Test
    void vote() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        VoteRequest request = VoteRequest.builder()
                .voteType(VoteType.FIRST)
                .voteAt(closedAt.minusDays(1))
                .build();

        // When
        Vote vote = topicService.vote(topic.getId(), member.getId(), request);

        // Then
        assertThat(vote.getVoteType()).isEqualTo(request.getVoteType());
    }

    @DisplayName("기존과 다른 투표 정보로 재투표를 하는 경우, 해당 정보로 변경된다.")
    @Test
    void voteWithDiffVoteType() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        Vote vote = Vote.builder()
                .voter(member)
                .topic(topic)
                .voteType(VoteType.FIRST)
                .build();
        voteRepository.save(vote);

        VoteRequest request = VoteRequest.builder()
                .voteType(VoteType.SECOND)
                .voteAt(closedAt.minusDays(1))
                .build();

        // When
        Vote resultVote = topicService.vote(topic.getId(), member.getId(), request);

        // Then
        assertThat(resultVote.getVoteType()).isEqualTo(resultVote.getVoteType());
    }

    @DisplayName("기존과 같은 투표 정보로 재투표를 하는 경우, null이 저장된다.")
    @Test
    void voteWithSameVoteType() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        Vote vote = Vote.builder()
                .voter(member)
                .topic(topic)
                .voteType(VoteType.FIRST)
                .build();
        voteRepository.save(vote);

        VoteRequest request = VoteRequest.builder()
                .voteType(VoteType.FIRST)
                .voteAt(closedAt.minusDays(1))
                .build();

        // When
        Vote resultVote = topicService.vote(topic.getId(), member.getId(), request);

        // Then
        assertThat(resultVote.getVoteType()).isNull();
    }

    @DisplayName("이미 종료된 토론에 투표하는 경우 에러가 발생한다.")
    @Test
    void voteOnOverTime() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);
        Topic topic = createTopic("title", "test", closedAt, member, category);
        topicRepository.save(topic);

        VoteRequest request = VoteRequest.builder()
                .voteType(VoteType.FIRST)
                .voteAt(closedAt.plusDays(1))
                .build();

        // When / Then
        assertThatThrownBy(() -> topicService.vote(topic.getId(), member.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.ALREADY_CLOSED.getMessage());
    }

    @DisplayName("유저 정보와 PageRequest를 전달하면, 해당 유저가 작성한 주제를 페이징하여 제공한다.")
    @Test
    void getMyTopics() {
        // Given
        Member member = createMember();
        Category category = createCategory();
        LocalDateTime closedAt = LocalDateTime.of(2024, 1, 11, 12, 0);
        Topic topic = createTopic("test", "test", closedAt, member, category);
        topicRepository.save(topic);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        PageItem myTopics = topicService.getMyTopics(member.getId(), pageRequest);

        // Then
        assertThat(myTopics)
                .extracting("results", "isFirst", "isLast", "isEmpty", "totalPage", "totalElements")
                .contains(myTopics.getResults(), true, true, false, 1, 1);
        assertThat(myTopics.getResults()).hasSize(1)
                .extracting("topicId", "title", "viewCount", "commentCount", "favoriteCount", "voteCount", "closedAt", "isClosed")
                .containsExactlyInAnyOrder(
                        tuple(topic.getId(), "test", 0L, 0L, 0L, 0L, closedAt, false)
                );
    }

    private Topic createTopic(String title, String description, LocalDateTime closedAt, Member member, Category category) {
        return Topic.builder()
                .title(title)
                .description(description)
                .closedAt(closedAt)
                .now(closedAt.minusDays(1))
                .firstChoice("first")
                .secondChoice("second")
                .author(member)
                .category(category)
                .build();
    }

    private Category createCategory() {
        Category category = Category.builder()
                .name("test")
                .build();
        return categoryRepository.save(category);
    }

    private Member createMember() {
        Member member = Member.builder()
                .accountId("tester1")
                .username("tester1")
                .email("test1@test.com")
                .password("test1")
                .build();
        return memberRepository.save(member);
    }

    private Member createSubMember() {
        Member member = Member.builder()
                .accountId("tester2")
                .username("tester2")
                .email("test2@test.com")
                .password("test")
                .build();
        return memberRepository.save(member);
    }
}