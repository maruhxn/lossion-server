package com.maruhxn.lossion.domain.auth.dao;

import com.maruhxn.lossion.domain.auth.domain.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByPayload(String payload);

    Optional<AuthToken> findByPayloadAndMember_Id(String payload, Long memberId);

    void deleteAllByMember_Id(Long memberId);
}
