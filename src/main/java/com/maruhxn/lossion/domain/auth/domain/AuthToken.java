package com.maruhxn.lossion.domain.auth.domain;

import com.maruhxn.lossion.domain.member.domain.Member;
import com.maruhxn.lossion.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AuthToken extends BaseEntity {
    private String payload;
    private LocalDateTime expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Builder
    public AuthToken(String payload, Member member) {

        Assert.hasText(payload, "payload는 필수입니다.");
        Assert.notNull(member, "유저는 필수입니다.");

        this.payload = payload;
        this.member = member;
        this.expiredAt = LocalDateTime.now().plusSeconds(10); // 5분 뒤 만료
    }

    // 편의 메서드 //
    public Boolean invalidate() {
        return this.getExpiredAt().isBefore(LocalDateTime.now());
    }
}
