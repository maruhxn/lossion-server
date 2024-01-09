package com.maruhxn.lossion.domain.member.application;

import com.maruhxn.lossion.domain.member.dao.MemberRepository;
import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.domain.member.dto.request.UpdateMemberProfileReq;
import com.maruhxn.lossion.domain.member.dto.request.UpdatePasswordReq;
import com.maruhxn.lossion.domain.member.dto.response.ProfileItem;
import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.BadRequestException;
import com.maruhxn.lossion.global.error.exception.EntityNotFoundException;
import com.maruhxn.lossion.infra.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.maruhxn.lossion.global.common.Constants.BASIC_PROFILE_IMAGE_NAME;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FileServiceImpl fileService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ProfileItem getProfile(Long memberId) {
        Member findMember = findMemberOrElseThrowById(memberId);

        return ProfileItem.from(findMember);
    }

    public void updateProfile(Long memberId, UpdateMemberProfileReq updateMemberProfileReq) {
        validateUpdateProfileRequest(updateMemberProfileReq);

        Member findMember = findMemberOrElseThrowById(memberId);

        String newProfileImageName = updateProfileImageLogic(updateMemberProfileReq, findMember);

        findMember.updateProfile(
                updateMemberProfileReq.getUsername(),
                updateMemberProfileReq.getEmail(),
                newProfileImageName);
    }

    public void updatePassword(Long memberId, UpdatePasswordReq updatePasswordReq) {
        Member findMember = findMemberOrElseThrowById(memberId);

        if (!updatePasswordReq.getNewPassword().equals(updatePasswordReq.getConfirmNewPassword())) {
            throw new BadRequestException(ErrorCode.PASSWORD_CONFIRM_FAIL);
        }

        if (passwordEncoder.matches(
                updatePasswordReq.getNewPassword(),
                findMember.getPassword()))
            throw new BadRequestException(ErrorCode.SAME_PASSWORD);

        if (!passwordEncoder.matches(
                updatePasswordReq.getCurrPassword(),
                findMember.getPassword()))
            throw new BadRequestException(ErrorCode.INCORRECT_PASSWORD);

        findMember.updatePassword(
                passwordEncoder.encode(updatePasswordReq.getNewPassword())
        );
    }

    public void membershipWithdrawal(Long memberId) {
        Member findMember = findMemberOrElseThrowById(memberId);
        deleteProfileImageOfFindMember(findMember);
        memberRepository.delete(findMember);
    }

    private Member findMemberOrElseThrowById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
    }

    private String updateProfileImageLogic(UpdateMemberProfileReq updateMemberProfileReq, Member findMember) {
        String newProfileImageName = null;
        MultipartFile newProfileImage = updateMemberProfileReq.getProfileImage();

        if (newProfileImage != null) {
            newProfileImageName = fileService.saveAndExtractUpdatedProfileImage(newProfileImage);
            // 기존 이미지 삭제
            deleteProfileImageOfFindMember(findMember);
        }

        return newProfileImageName;
    }

    private void validateUpdateProfileRequest(UpdateMemberProfileReq updateMemberProfileReq) {
        if (
                updateMemberProfileReq.getUsername() == null
                        && updateMemberProfileReq.getProfileImage() == null
                        && updateMemberProfileReq.getEmail() == null) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }
    }

    private void deleteProfileImageOfFindMember(Member findMember) {
        if (!Objects.equals(findMember.getProfileImage(), BASIC_PROFILE_IMAGE_NAME)) {
            fileService.deleteFile(findMember.getProfileImage());
        }
    }
}
