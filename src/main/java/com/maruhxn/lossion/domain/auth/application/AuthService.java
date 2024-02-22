package com.maruhxn.lossion.domain.auth.application;

import com.maruhxn.lossion.domain.auth.dao.AuthTokenRepository;
import com.maruhxn.lossion.domain.auth.domain.AuthToken;
import com.maruhxn.lossion.domain.auth.dto.*;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.member.dto.request.UpdateAnonymousPasswordReq;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.AlreadyExistsResourceException;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.global.error.exception.ExpirationException;
import com.maruhxn.lossion.global.util.AesUtil;
import com.maruhxn.lossion.infra.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void signUp(SignUpReq req) {
        // 제약조건 검사
        validatePassword(req.getPassword(), req.getConfirmPassword());
        String hashedPwd = passwordEncoder.encode(req.getPassword());

        Optional<Member> optionalMember = memberRepository.findByEmail(req.getEmail());

        if (optionalMember.isPresent()) { // 찾은 멤버가 소셜 로그인을 시도한 적이 있는 경우, -> 아이디, 유저명, 비밀번호 덮어쓰기.
            Member findMember = optionalMember.get();
            if (findMember.getSnsId() != null) {
                findMember.rewriteOAuth2User(req.getUsername(), req.getAccountId(), hashedPwd);
            } else {
                throw new AlreadyExistsResourceException(ErrorCode.EXISTING_EMAIL);
            }
        } else {
            uniqueMemberCheck(req);
            req.changeRawPwdToHashedPwd(hashedPwd);

            Member member = Member.from(req);
            memberRepository.save(member);
        }
    }

    private static void validatePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException(ErrorCode.PASSWORD_CONFIRM_FAIL);
        }
    }

    private void uniqueMemberCheck(SignUpReq req) {
        Boolean existedAccountId = memberRepository.existsByAccountId(req.getAccountId());
        if (existedAccountId) {
            throw new AlreadyExistsResourceException(ErrorCode.EXISTING_ID);
        }

        Boolean existedUsername = memberRepository.existsByUsername(req.getUsername());
        if (existedUsername) {
            throw new AlreadyExistsResourceException(ErrorCode.EXISTING_USERNAME);
        }
    }

    public void sendVerifyEmailWithLogin(Member member, LocalDateTime now) {
        if (member.getIsVerified()) throw new AlreadyExistsResourceException(ErrorCode.ALREADY_VERIFIED);
        sendMail(member, now);
    }

    public void sendVerifyEmailWithAnonymous(SendAnonymousEmailReq req, LocalDateTime now) {
        Member findMember = memberRepository.findByAccountIdAndEmail(
                req.getAccountId(),
                req.getEmail()
        ).orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        sendMail(findMember, now);
    }

    private void sendMail(Member findMember, LocalDateTime now) {
        String payload = String.valueOf((int) Math.floor(100000 + Math.random() * 900000));

        emailService.sendEmail(findMember.getEmail(), payload);

        AuthToken authToken = AuthToken.builder()
                .payload(payload)
                .expiredAt(now.plusMinutes(5))
                .member(findMember)
                .build();

        authTokenRepository.save(authToken);
    }

    public void verifyEmail(Long memberId, VerifyEmailReq req, LocalDateTime now) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        if (member.getIsVerified()) throw new AlreadyExistsResourceException(ErrorCode.ALREADY_VERIFIED);

        AuthToken findAuthToken = authTokenRepository.findByPayloadAndMember_Id(req.getPayload(), member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOKEN));

        validateAuthToken(findAuthToken, now);

        // 인증 완료
        member.verifyEmail();

        authTokenRepository.deleteAllByMember_Id(member.getId());
    }

    public void verifyPassword(Member member, VerifyPasswordReq req) {
        if (!passwordEncoder.matches(req.getCurrPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorCode.PASSWORD_CONFIRM_FAIL);
        }
    }

    public String getAuthKeyToFindPassword(GetTokenReq req, LocalDateTime now) {

        Member findMember = memberRepository.findByAccountIdAndEmail(
                req.getAccountId(),
                req.getEmail()
        ).orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        AuthToken findAuthToken = authTokenRepository.findByPayloadAndMember_Id(req.getPayload(), findMember.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOKEN));

        validateAuthToken(findAuthToken, now);

        return AesUtil.encrypt(findAuthToken.getPayload());
    }

    public void updateAnonymousPassword(String authKey, UpdateAnonymousPasswordReq req) {

        String payload = AesUtil.decrypt(authKey);

        AuthToken findAuthToken = authTokenRepository.findByPayload(payload)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_TOKEN));

        validatePassword(req.getNewPassword(), req.getConfirmNewPassword());

        Member findMember = findAuthToken.getMember();

        String hashedPwd = passwordEncoder.encode(req.getNewPassword());
        findMember.updatePassword(hashedPwd);

        authTokenRepository.deleteAllByMember_Id(findMember.getId());

    }

    private static void validateAuthToken(AuthToken findAuthToken, LocalDateTime now) {
        if (findAuthToken.invalidate(now)) {
            // TODO: 만료된 토큰이 쌓이는 것을 방지하기 위해 바로바로 삭제하고 싶지만, transaction으로 인해 현재 상태에서는 불가능하다. 트랜잭션을 분리할 방법을 생각해보자.
            throw new ExpirationException(ErrorCode.TOKEN_EXPIRATION);
        }
    }

    public void checkHasPassword(Member member) {
        if (!StringUtils.hasText(member.getPassword())) {
            throw new BadRequestException(ErrorCode.NEED_PASSWORD);
        }
    }
}
