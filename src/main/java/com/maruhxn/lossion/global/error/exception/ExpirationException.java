package com.maruhxn.lossion.global.error.exception;

import com.maruhxn.lossion.global.error.ErrorCode;

public class ExpirationException extends GlobalException {
    public ExpirationException(ErrorCode code) {
        super(code);
    }
}
