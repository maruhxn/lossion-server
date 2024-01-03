package com.maruhxn.lossion.global.error.exception;

import com.maruhxn.lossion.global.error.ErrorCode;

public class AlreadyExistsResourceException extends GlobalException {
    public AlreadyExistsResourceException(ErrorCode code) {
        super(code);
    }
}