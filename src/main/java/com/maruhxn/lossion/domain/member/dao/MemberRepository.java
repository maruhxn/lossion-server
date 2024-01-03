package com.maruhxn.lossion.domain.member.dao;

import com.maruhxn.lossion.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
