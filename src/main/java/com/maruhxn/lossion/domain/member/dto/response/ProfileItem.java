package com.maruhxn.lossion.domain.member.dto.response;

import com.maruhxn.lossion.domain.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileItem {

    private Long id;
    private String accountId;
    private String email;
    private String username;
    private Boolean isVerified;
    private String profileImage;

    @Builder
    public ProfileItem(Long id, String accountId, String email, String username, Boolean isVerified, String profileImage) {
        this.id = id;
        this.accountId = accountId;
        this.email = email;
        this.username = username;
        this.isVerified = isVerified;
        this.profileImage = profileImage;
    }

    public static ProfileItem from(Member member) {
        return ProfileItem.builder()
                .id(member.getId())
                .accountId(member.getAccountId())
                .email(member.getEmail())
                .username(member.getUsername())
                .isVerified(member.getIsVerified())
                .profileImage(member.getProfileImage())
                .build();
    }
}
