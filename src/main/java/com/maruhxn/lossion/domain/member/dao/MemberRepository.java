package com.maruhxn.lossion.domain.member.dao;

import com.maruhxn.lossion.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // exists

    Boolean existsByAccountId(String accountId);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByTelNumber(String telNumber);

    // find

    Optional<Member> findByEmail(String email);

    Optional<Member> findByAccountId(String accountId);

    Optional<Member> findByAccountIdAndEmail(String accountId, String email);


    // count
    Long countByUsername(String username);
}
