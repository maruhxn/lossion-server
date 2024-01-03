package com.maruhxn.lossion.global.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseResponse {
    private String code;
    private String message;

    public BaseResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static BaseResponse of(String message) {
        return new BaseResponse("OK", message);
    }
}