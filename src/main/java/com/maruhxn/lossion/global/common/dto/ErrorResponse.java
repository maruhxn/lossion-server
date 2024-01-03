package com.maruhxn.lossion.global.common.dto;

import com.maruhxn.lossion.global.common.FieldError;
import com.maruhxn.lossion.global.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.BindingResult;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ErrorResponse extends BaseResponse {
    private List<FieldError> errors;

    public ErrorResponse(ErrorCode errorCode) {
        super(errorCode.name(), errorCode.getMessage());
        this.errors = List.of();
    }

    public ErrorResponse(ErrorCode errorCode, String message) {
        super(errorCode.name(), message);
        this.errors = List.of();
    }

    public ErrorResponse(ErrorCode errorCode, List<FieldError> errors) {
        super(errorCode.name(), errorCode.getMessage());
        this.errors = errors;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }

    public static ErrorResponse validationError(BindingResult bindingResult) {
        return new ErrorResponse(ErrorCode.VALIDATION_ERROR, FieldError.of(bindingResult));
    }

}
