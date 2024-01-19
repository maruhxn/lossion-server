package com.maruhxn.lossion.domain.member.api;

import com.maruhxn.lossion.domain.member.application.MemberService;
import com.maruhxn.lossion.domain.member.dto.request.UpdateMemberProfileReq;
import com.maruhxn.lossion.domain.member.dto.request.UpdatePasswordReq;
import com.maruhxn.lossion.domain.member.dto.response.ProfileItem;
import com.maruhxn.lossion.global.common.dto.DataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
@PreAuthorize("(principal.getId() == #memberId) or hasRole('ROLE_ADMIN')")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<DataResponse<ProfileItem>> getProfile(
            @PathVariable Long memberId
    ) {
        ProfileItem profile = memberService.getProfile(memberId);
        return ResponseEntity.ok(DataResponse.of("프로필 조회 성공", profile));
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfile(
            @PathVariable("memberId") Long memberId,
            @ModelAttribute @Valid UpdateMemberProfileReq updateMemberProfileReq
    ) {
        memberService.updateProfile(memberId, updateMemberProfileReq);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(
            @PathVariable("memberId") Long memberId,
            @Valid @RequestBody UpdatePasswordReq updatePasswordReq
    ) {
        memberService.updatePassword(memberId, updatePasswordReq);
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(
            @PathVariable("memberId") Long memberId
    ) {

        log.info("회원 탈퇴 | memberId={}", memberId);
        memberService.membershipWithdrawal(memberId);
    }
}
