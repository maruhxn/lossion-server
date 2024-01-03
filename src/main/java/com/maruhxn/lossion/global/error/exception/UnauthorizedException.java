package com.maruhxn.lossion.global.error.exception;

import com.maruhxn.lossion.global.error.ErrorCode;

public class UnauthorizedException extends GlobalException {
    public UnauthorizedException(ErrorCode code) {
        super(code);
    }
}