package com.maruhxn.lossion.domain.auth.api;

import com.maruhxn.lossion.domain.auth.application.AuthService;
import com.maruhxn.lossion.domain.auth.dto.SignUpReq;
import com.maruhxn.lossion.domain.auth.dto.VerifyEmailReq;
import com.maruhxn.lossion.domain.auth.dto.VerifyPasswordReq;
import com.maruhxn.lossion.global.auth.application.JwtService;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.auth.dto.TokenDto;
import com.maruhxn.lossion.global.common.dto.BaseResponse;
import com.maruhxn.lossion.global.common.dto.DataResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.maruhxn.lossion.global.common.Constants.REFRESH_TOKEN_HEADER;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> signUp(
            @RequestBody @Valid SignUpReq req
    ) {
        authService.signUp(req);
        return new ResponseEntity<>(new BaseResponse("회원가입 성공"), HttpStatus.CREATED);
    }

    @GetMapping("/refresh")
    public ResponseEntity<DataResponse<TokenDto>> refresh(
            HttpServletResponse response,
            @RequestHeader(value = REFRESH_TOKEN_HEADER, required = true) String bearerRefreshToken
    ) {
        TokenDto tokenDto = jwtService.refresh(bearerRefreshToken, response);
        return ResponseEntity.ok(DataResponse.of("Token Refresh 성공", tokenDto));
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

    @PostMapping("/verify-password")
    public ResponseEntity<BaseResponse> veirfyPassword(
            @AuthenticationPrincipal JwtMemberInfo memberInfo,
            @RequestBody @Valid VerifyPasswordReq req
    ) {
        authService.verifyPassword(memberInfo, req);
        return ResponseEntity.ok(new BaseResponse("비밀번호 인증 성공"));
    }

    @PatchMapping("/logout")
    public ResponseEntity<BaseResponse> logout(
            @RequestHeader(value = REFRESH_TOKEN_HEADER, required = true) String bearerRefreshToken
    ) {
        jwtService.logout(bearerRefreshToken);
        return new ResponseEntity<>(new BaseResponse("로그아웃 성공"), HttpStatus.NO_CONTENT);
    }
}
