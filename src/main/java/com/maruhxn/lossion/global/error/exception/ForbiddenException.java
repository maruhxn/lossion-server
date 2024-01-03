package com.maruhxn.lossion.global.error.exception;

import com.maruhxn.lossion.global.error.ErrorCode;

public class ForbiddenException extends GlobalException {
    public ForbiddenException(ErrorCode code) {
        super(code);
    }
}