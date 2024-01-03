package com.maruhxn.lossion.global.error.exception;

import com.maruhxn.lossion.global.error.ErrorCode;

public class EntityNotFoundException extends GlobalException {
    public EntityNotFoundException(ErrorCode code) {
        super(code);
    }
}