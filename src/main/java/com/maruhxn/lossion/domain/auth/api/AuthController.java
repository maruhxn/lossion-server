package com.maruhxn.lossion.domain.auth.api;

import com.maruhxn.lossion.domain.auth.application.AuthService;
import com.maruhxn.lossion.domain.auth.dto.SignUpReq;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.common.dto.BaseResponse;
import com.maruhxn.lossion.global.common.dto.DataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> signUp(
            @RequestBody @Valid SignUpReq req
    ) {
        authService.signUp(req);
        return new ResponseEntity<>(new BaseResponse("회원가입 성공"), HttpStatus.CREATED);
    }

    @GetMapping("/test")
    public DataResponse<JwtMemberInfo> test(
            @AuthenticationPrincipal JwtMemberInfo jwtMemberInfo
    ) {
        return DataResponse.of("유저 정보", jwtMemberInfo);
    }
}
