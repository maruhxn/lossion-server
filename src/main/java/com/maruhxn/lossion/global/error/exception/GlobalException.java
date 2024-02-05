package com.maruhxn.lossion.global.error.exception;

import com.maruhxn.lossion.global.error.ErrorCode;
import lombok.Getter;

@Getter
public abstract class GlobalException extends RuntimeException {
    private final ErrorCode code;

    protected GlobalException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    protected GlobalException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    protected GlobalException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    protected GlobalException(ErrorCode code, Throwable cause) {
        super(cause.getMessage(), cause);
        this.code = code;
    }
}