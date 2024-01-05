package com.maruhxn.lossion.domain.auth.dao;

import com.maruhxn.lossion.domain.auth.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByPayloadAndMember_Id(String payload, Long memberId);

    void deleteAllByMember_Id(Long memberId);
}
