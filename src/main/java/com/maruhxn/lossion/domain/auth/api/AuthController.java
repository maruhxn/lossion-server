package com.maruhxn.lossion.domain.auth.api;

import com.maruhxn.lossion.domain.auth.application.AuthService;
import com.maruhxn.lossion.domain.auth.dto.SignUpReq;
import com.maruhxn.lossion.domain.auth.dto.VerifyEmailReq;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.common.dto.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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

    @GetMapping("/send-verify-email")
    public ResponseEntity<BaseResponse> sendVerifyEmail(
            @AuthenticationPrincipal JwtMemberInfo memberInfo
    ) {
        authService.sendVerifyEmail(memberInfo);
        return ResponseEntity.ok(new BaseResponse("인증 메일 발송 성공"));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<BaseResponse> verifyEmail(
            @AuthenticationPrincipal JwtMemberInfo memberInfo,
            @RequestBody @Valid VerifyEmailReq req
    ) {
        authService.verifyEmail(memberInfo, req);
        return ResponseEntity.ok(new BaseResponse("이메일 인증 성공"));
    }
}
