package com.maruhxn.lossion.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /* BAD REQUEST 400 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 접근입니다."),
    PASSWORD_CONFIRM_FAIL(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "이메일 혹은 비밀번호가 올바르지 않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "이전과 동일한 비밀번호로 변경할 수 없습니다."),
    SPRING_BAD_REQUEST(HttpStatus.BAD_REQUEST, "스프링 BAD REQUEST"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "올바르지 않은 입력입니다."),

    /* UNAUTHORIZED 401 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    /* FORBIDDEN 403 */
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    /* NOT FOUND 404 */
    NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, "요청하신 자원이 존재하지 않습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "유저 정보가 존재하지 않습니다."),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "토큰 정보가 올바르지 않습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "Refresh Token 정보가 존재하지 않습니다."),

    /* UNPROCESSABLE CONTENT 422 */
    EXISTING_RESOURCE(HttpStatus.UNPROCESSABLE_ENTITY, "이미 존재하는 리소스입니다."),
    EXISTING_ID(HttpStatus.UNPROCESSABLE_ENTITY, "이미 존재하는 아이디입니다."),
    EXISTING_EMAIL(HttpStatus.UNPROCESSABLE_ENTITY, "이미 존재하는 이메일입니다."),
    EXISTING_USERNAME(HttpStatus.UNPROCESSABLE_ENTITY, "이미 존재하는 유저명입니다."),
    EXISTING_TEL(HttpStatus.UNPROCESSABLE_ENTITY, "이미 존재하는 전화번호입니다."),
    ALREADY_VERIFIED(HttpStatus.UNPROCESSABLE_ENTITY, "이미 인증된 이메일입니다."),
    TOKEN_EXPIRATION(HttpStatus.UNPROCESSABLE_ENTITY, "이미 만료된 토큰입니다"),

    /* INTERNAL SERVER ERROR  500 */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),
    SPRING_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "스프링 오류 입니다."),
    DATA_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류입니다.");

    private final HttpStatus httpStatus;
    private final String message;

}