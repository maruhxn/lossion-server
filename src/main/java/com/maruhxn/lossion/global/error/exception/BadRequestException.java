package com.maruhxn.lossion.global.error.exception;

import com.maruhxn.lossion.global.error.ErrorCode;

public class BadRequestException extends GlobalException {
    public BadRequestException(ErrorCode code) {
        super(code);
    }
}
