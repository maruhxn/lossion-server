package com.maruhxn.lossion.global.error;

import com.maruhxn.lossion.global.common.dto.ErrorResponse;
import com.maruhxn.lossion.global.error.exception.GlobalException;
import com.maruhxn.lossion.global.error.exception.InternalServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[핸들러] - GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler sut;

    @BeforeEach
    void setUp() {
        sut = new GlobalExceptionHandler();
    }


    @Test
    @DisplayName("권한 오류")
    void shouldReturnAccessDeniedResponseWhenOccursAccessDeninedException() {
        // Given
        ErrorCode errorCode = ErrorCode.FORBIDDEN;
        AccessDeniedException e = new AccessDeniedException("FORBIDDEN");

        // When
        ResponseEntity<Object> response = sut.accessDenied(e);

        // Then
        assertResponseWithErrorCode(response, errorCode);
    }

    @Test
    @DisplayName("404 에러")
    void shouldReturnNotFoundResponseWhenCannotFindResource() {
        // Given
        ErrorCode errorCode = ErrorCode.NOT_FOUND_RESOURCE;
        NoHandlerFoundException e = new NoHandlerFoundException(null, null, null);

        // When
        ResponseEntity<Object> response = sut.handle404(e);

        // Then
        assertResponseWithErrorCode(response, errorCode);
    }

    @Test
    @DisplayName("데이터베이스 유니크 제약조건 에러")
    void dataIntegrityViolation() {
        // Given
        ErrorCode errorCode = ErrorCode.EXISTING_RESOURCE;
        DataIntegrityViolationException e = new DataIntegrityViolationException("DataIntegrityViolationException");

        // When
        ResponseEntity<Object> response = sut.dataIntegrityViolation(e);

        // Then
        assertResponseWithErrorCode(response, errorCode);
    }

    @Test
    @DisplayName("유효성 검증 실패 에러")
    void validationFail() {
        // Given
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        MethodArgumentNotValidException e = new MethodArgumentNotValidException(null, new BeanPropertyBindingResult(null, null));

        // When
        ResponseEntity<Object> response = sut.validationFail(e);
        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("body", ErrorResponse.validationError(e.getBindingResult()))
                .hasFieldOrPropertyWithValue("headers", HttpHeaders.EMPTY)
                .hasFieldOrPropertyWithValue("statusCode", errorCode.getHttpStatus());
    }

    @Test
    @DisplayName("비즈니스 에러")
    void globalException() {
        // Given
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        GlobalException e = new InternalServerException(errorCode);

        // When
        ResponseEntity<Object> response = sut.globalException(e);

        // Then
        assertResponseWithErrorCode(response, errorCode);
    }

    @Test
    @DisplayName("예상하지 못한 에러")
    void exception() {
        // Given
        RuntimeException e = new RuntimeException("RuntimeException");

        // When
        ResponseEntity<Object> response = sut.exception(e);

        // Then
        assertResponseWithErrorCode(response, ErrorCode.INTERNAL_ERROR);
    }

    private static void assertResponseWithErrorCode(ResponseEntity<Object> response, ErrorCode errorCode) {
        assertThat(response)
                .hasFieldOrPropertyWithValue("body", ErrorResponse.of(errorCode))
                .hasFieldOrPropertyWithValue("headers", HttpHeaders.EMPTY)
                .hasFieldOrPropertyWithValue("statusCode", errorCode.getHttpStatus());
    }
}