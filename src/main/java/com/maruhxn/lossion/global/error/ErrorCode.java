package com.maruhxn.lossion.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /* BAD REQUEST 400 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 접근입니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),
    PASSWORD_CONFIRM_FAIL(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "이전과 동일한 비밀번호로 변경할 수 없습니다."),
    ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "이미 종료된 투표입니다."),
    SPRING_BAD_REQUEST(HttpStatus.BAD_REQUEST, "스프링 BAD REQUEST"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "올바르지 않은 입력입니다."),
    PATH_VAR_ERROR(HttpStatus.BAD_REQUEST, "올바르지 않은 URI입니다."),
    EMPTY_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refresh token이 필요합니다."),
    NEED_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호 설정이 필요합니다."),
    ALREADY_EXIST_PASSWORD(HttpStatus.BAD_REQUEST, "이미 비밀번호가 설정되어 있습니다."),

    /* UNAUTHORIZED 401 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    /* FORBIDDEN 403 */
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    UNVERIFIED_EMAIL(HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다."),

    /* NOT FOUND 404 */
    NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, "요청하신 자원이 존재하지 않습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "유저 정보가 존재하지 않습니다."),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "토큰 정보가 올바르지 않습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "Refresh Token 정보가 존재하지 않습니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "카테고리 정보가 존재하지 않습니다."),
    NOT_FOUND_TOPIC(HttpStatus.NOT_FOUND, "주제 정보가 존재하지 않습니다."),
    NOT_FOUND_TOPIC_IMAGE(HttpStatus.NOT_FOUND, "이미지 정보가 존재하지 않습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글 정보가 존재하지 않습니다."),
    NOT_FOUND_FAVORITE(HttpStatus.NOT_FOUND, "좋아요 정보가 존재하지 않습니다."),
    NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다."),
    NOT_FOUND_VOTE(HttpStatus.NOT_FOUND, "투표 정보가 존재하지 않습니다."),
    /* UNPROCESSABLE CONTENT 422 */
    EXISTING_RESOURCE(HttpStatus.UNPROCESSABLE_ENTITY, "이미 존재하는 리소스입니다."),
    EXISTING_ID(HttpStatus.UNPROCESSABLE_ENTITY, "이미 존재하는 아이디입니다."),
    EXISTING_EMAIL(HttpStatus.UNPROCESSABLE_ENTITY, "이미 존재하는 이메일입니다."),
    EXISTING_USERNAME(HttpStatus.UNPROCESSABLE_ENTITY, "이미 존재하는 유저명입니다."),
    EXISTING_CATEGORY(HttpStatus.UNPROCESSABLE_ENTITY, "이미 존재하는 카테고리입니다."),
    ALREADY_VERIFIED(HttpStatus.UNPROCESSABLE_ENTITY, "이미 인증된 이메일입니다."),
    TOKEN_EXPIRATION(HttpStatus.UNPROCESSABLE_ENTITY, "이미 만료된 토큰입니다"),

    /* INTERNAL SERVER ERROR  500 */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),
    SPRING_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "스프링 오류 입니다."),
    DATA_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류입니다."),
    S3_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 문제가 발생했습니다."),
    MAIL_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송 중 문제가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}