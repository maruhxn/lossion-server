package com.maruhxn.lossion.domain.auth.application;

import com.maruhxn.lossion.domain.auth.dto.SignUpReq;
import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.AlreadyExistsResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
}
