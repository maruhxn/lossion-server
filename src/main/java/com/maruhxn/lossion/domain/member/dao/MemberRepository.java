package com.maruhxn.lossion.domain.member.dao;

import com.maruhxn.lossion.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByAccountId(String accountId);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByTelNumber(String telNumber);

    Optional<Member> findByAccountId(String accountId);

    Optional<Member> findByAccountIdAndEmail(String accountId, String email);

}
