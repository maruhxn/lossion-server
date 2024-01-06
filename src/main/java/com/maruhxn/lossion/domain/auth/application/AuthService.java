package com.maruhxn.lossion.domain.auth.application;

import com.maruhxn.lossion.domain.auth.dao.TokenRepository;
import com.maruhxn.lossion.domain.auth.domain.Token;
import com.maruhxn.lossion.domain.auth.dto.SignUpReq;
import com.maruhxn.lossion.domain.auth.dto.VerifyEmailReq;
import com.maruhxn.lossion.domain.auth.dto.VerifyPasswordReq;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.auth.dto.JwtMemberInfo;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.AlreadyExistsResourceException;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.global.error.exception.ExpirationException;
import com.maruhxn.lossion.infra.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void signUp(SignUpReq req) {
        // 제약조건 검사
        uniqueMemberCheck(req);

        String hashedPwd = passwordEncoder.encode(req.getPassword());
        req.changeRawPwdToHashedPwd(hashedPwd);

        Member member = Member.from(req);
        memberRepository.save(member);
    }

    private void uniqueMemberCheck(SignUpReq req) {
        Boolean existedAccountId = memberRepository.existsByAccountId(req.getAccountId());
        if (existedAccountId) {
            throw new AlreadyExistsResourceException(ErrorCode.EXISTING_ID);
        }

        Boolean existedEmail = memberRepository.existsByEmail(req.getEmail());
        if (existedEmail) {
            throw new AlreadyExistsResourceException(ErrorCode.EXISTING_EMAIL);
        }

        Boolean existedUsername = memberRepository.existsByUsername(req.getUsername());
        if (existedUsername) {
            throw new AlreadyExistsResourceException(ErrorCode.EXISTING_USERNAME);
        }

        Boolean existedTelNumber = memberRepository.existsByTelNumber(req.getTelNumber());
        if (existedTelNumber) {
            throw new AlreadyExistsResourceException(ErrorCode.EXISTING_TEL);
        }
    }

    public void sendVerifyEmail(JwtMemberInfo memberInfo) {
        if (memberInfo.getIsVerified()) throw new AlreadyExistsResourceException(ErrorCode.ALREADY_VERIFIED);
        Member findMember = memberRepository.findByAccountId(memberInfo.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));

        Token token = Token.builder()
                .payload(payload)
                .member(findMember)
                .build();

        tokenRepository.save(token);

        emailService.sendEmail(memberInfo.getEmail(), "Authentication Code : " + payload);
    }

    public void verifyEmail(JwtMemberInfo memberInfo, VerifyEmailReq req) {
        if (memberInfo.getIsVerified()) throw new AlreadyExistsResourceException(ErrorCode.ALREADY_VERIFIED);
        Member findMember = memberRepository.findByAccountId(memberInfo.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        Token findToken = tokenRepository.findByPayloadAndMember_Id(req.getPayload(), findMember.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOKEN));

        if (findToken.invalidate()) {
            // TODO: 만료된 토큰이 쌓이는 것을 방지하기 위해 바로바로 삭제하고 싶지만, transaction으로 인해 현재 상태에서는 불가능하다. 트랜잭션을 분리할 방법을 생각해보자.
            throw new ExpirationException(ErrorCode.TOKEN_EXPIRATION);
        }

        // 인증 완료
        findMember.verifyEmail();

        tokenRepository.deleteAllByMember_Id(findMember.getId());
    }

    public void verifyPassword(JwtMemberInfo memberInfo, VerifyPasswordReq req) {
        Member findMember = memberRepository.findByAccountId(memberInfo.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        if (!passwordEncoder.matches(req.getCurrPassword(), findMember.getPassword())) {
            throw new BadRequestException(ErrorCode.PASSWORD_CONFIRM_FAIL);
        }
    }
}
