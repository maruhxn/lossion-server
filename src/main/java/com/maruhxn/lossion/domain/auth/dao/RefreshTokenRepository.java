package com.maruhxn.lossion.domain.auth.dao;

import com.maruhxn.lossion.domain.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByEmail(String email);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteAllByAccountId(String accountId);

}
