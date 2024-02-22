package com.maruhxn.lossion.domain.topic.dto.response;

import com.maruhxn.lossion.domain.member.domain.Member;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class AuthorInfoItem {
    private Long authorId;
    private String username;
    private String profileImage;
    private String accountId;

    @Builder
    public AuthorInfoItem(Long authorId, String username, String profileImage, String accountId) {
        this.authorId = authorId;
        this.username = username;
        this.profileImage = profileImage;
        this.accountId = accountId;
    }

    public static AuthorInfoItem from(Member author) {
        return AuthorInfoItem.builder()
                .authorId(author.getId())
                .username(author.getUsername())
                .profileImage(author.getProfileImage())
                .accountId(author.getAccountId())
                .build();
    }
}
