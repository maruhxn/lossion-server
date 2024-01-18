package com.maruhxn.lossion.global.error;

import com.maruhxn.lossion.global.common.dto.ErrorResponse;
import com.maruhxn.lossion.global.error.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Access Denied 핸들러
    @ExceptionHandler
    public ResponseEntity<Object> accessDenied(AccessDeniedException e) {
        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getHttpStatus())
                .body(ErrorResponse.of(ErrorCode.FORBIDDEN));
    }

    // 404예외처리 핸들러
    @ExceptionHandler
    public ResponseEntity<Object> handle404(NoHandlerFoundException e) {
        return ResponseEntity
                .status(ErrorCode.NOT_FOUND_RESOURCE.getHttpStatus())
                .body(ErrorResponse.of(ErrorCode.NOT_FOUND_RESOURCE));
    }

    @ExceptionHandler
    public ResponseEntity<Object> dataIntegrityViolation(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(ErrorCode.EXISTING_RESOURCE.getHttpStatus())
                .body(ErrorResponse.of(ErrorCode.EXISTING_RESOURCE));
    }

    @ExceptionHandler
    public ResponseEntity<Object> constraintViolation(ConstraintViolationException e) {
        return ResponseEntity
                .status(ErrorCode.EXISTING_RESOURCE.getHttpStatus())
                .body(ErrorResponse.of(ErrorCode.EXISTING_RESOURCE));
    }

    @ExceptionHandler
    public ResponseEntity<Object> validationFail(MethodArgumentNotValidException e) {

        return ResponseEntity
                .status(ErrorCode.VALIDATION_ERROR.getHttpStatus())
                .body(ErrorResponse.validationError(e.getBindingResult()));
    }

    @ExceptionHandler
    public ResponseEntity<Object> pathVariableValidationFail(MethodArgumentTypeMismatchException e) {

        return ResponseEntity
                .status(ErrorCode.PATH_VAR_ERROR.getHttpStatus())
                .body(ErrorResponse.of(ErrorCode.PATH_VAR_ERROR));
    }

    @ExceptionHandler
    public ResponseEntity<Object> globalException(GlobalException e) {
        return ResponseEntity
                .status(e.getCode().getHttpStatus())
                .body(ErrorResponse.of(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getHttpStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, e.getMessage()));
    }

}
