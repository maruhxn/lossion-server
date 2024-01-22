package com.maruhxn.lossion.docs.support;

import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.InternalServerException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class GlobalDataDocsController {
    @PostMapping("/validation-error")
    public void errorSample(@RequestBody @Valid SampleRequest dto) {
    }

    @GetMapping("/internal-server-exception")
    public void globalExceptionSample() {
        throw new InternalServerException(ErrorCode.INTERNAL_ERROR);
    }

    @GetMapping("/error-code")
    public Map<String, ErrorCodeResponse> findEnums() {
        Map<String, ErrorCodeResponse> map = new HashMap<>();
        for (ErrorCode errorCode : ErrorCode.values()) {
            map.put(errorCode.name(), new ErrorCodeResponse(errorCode));
        }
        return map;
    }

    @Getter
    @NoArgsConstructor
    protected static class ErrorCodeResponse {
        private String code;
        private String message;
        private HttpStatus httpStatus;

        public ErrorCodeResponse(ErrorCode errorCode) {
            this.code = errorCode.name();
            this.message = errorCode.getMessage();
            this.httpStatus = errorCode.getHttpStatus();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SampleRequest {

        @NotEmpty
        private String name;

        @Email
        private String email;

        public SampleRequest(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
