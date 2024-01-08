package com.maruhxn.lossion.domain.auth.api;

import com.maruhxn.lossion.domain.auth.application.AuthService;
import com.maruhxn.lossion.domain.auth.dto.*;
import com.maruhxn.lossion.domain.member.dto.request.UpdateAnonymousPasswordReq;
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
        authService.sendVerifyEmailWithLogin(memberInfo);
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
    public ResponseEntity<BaseResponse> verifyPassword(
            @AuthenticationPrincipal JwtMemberInfo memberInfo,
            @RequestBody @Valid VerifyPasswordReq req
    ) {
        authService.verifyPassword(memberInfo, req);
        return ResponseEntity.ok(new BaseResponse("비밀번호 인증 성공"));
    }

    @PostMapping("/send-anonymous-verify-email")
    public ResponseEntity<BaseResponse> sendEmailWithAnonymous(
            @RequestBody @Valid SendAnonymousEmailReq req
    ) {
        authService.sendVerifyEmailWithAnonymous(req);
        return ResponseEntity.ok(new BaseResponse("인증 메일 발송 성공"));
    }

    /**
     * 비밀번호 찾기를 위한 토큰을 얻는 API
     * 이를 위해서는 입력한 이메일에 대한 인증 토큰이 필요하다.
     *
     * @param getTokenReq
     * @return
     */
    @PostMapping("/get-token")
    public ResponseEntity<DataResponse<String>> findPasswordByAccountIdAndEmail(
            @RequestBody @Valid GetTokenReq getTokenReq
    ) {
        String authToken = authService.findPasswordByAccountIdAndEmail(getTokenReq);
        return ResponseEntity.ok(DataResponse.of("유저 정보 찾기 성공", authToken));
    }

    /**
     * 비밀번호 찾기를 통해 익명으로 비밀번호를 변경하는 API
     * 쿼리 파라미터로 /get-token에서 발급받은 authToken을 넘겨주어야 한다.
     *
     * @param token
     * @param updateAnonymousPasswordReq
     */
    @PatchMapping("/update-anonymous-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAnonymousPassword(
            @RequestParam("token") String token,
            @RequestBody @Valid UpdateAnonymousPasswordReq updateAnonymousPasswordReq
    ) {
        authService.updateAnonymousPassword(token, updateAnonymousPasswordReq);
    }

    @PatchMapping("/logout")
    public ResponseEntity<BaseResponse> logout(
            @RequestHeader(value = REFRESH_TOKEN_HEADER, required = true) String bearerRefreshToken
    ) {
        jwtService.logout(bearerRefreshToken);
        return new ResponseEntity<>(new BaseResponse("로그아웃 성공"), HttpStatus.NO_CONTENT);
    }
}
