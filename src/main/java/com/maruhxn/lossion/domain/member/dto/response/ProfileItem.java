package com.maruhxn.lossion.domain.member.dto.response;

import com.maruhxn.lossion.domain.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileItem {

    private String accountId;
    private String email;
    private String username;
    private String telNumber;
    private Boolean isVerified;
    private String profileImage;

    @Builder
    public ProfileItem(String accountId, String email, String username, String telNumber, Boolean isVerified, String profileImage) {
        this.accountId = accountId;
        this.email = email;
        this.username = username;
        this.telNumber = telNumber;
        this.isVerified = isVerified;
        this.profileImage = profileImage;
    }

    public static ProfileItem from(Member member) {
        return ProfileItem.builder()
                .accountId(member.getAccountId())
                .email(member.getEmail())
                .username(member.getUsername())
                .telNumber(member.getTelNumber())
                .isVerified(member.getIsVerified())
                .profileImage(member.getProfileImage())
                .build();
    }
}
