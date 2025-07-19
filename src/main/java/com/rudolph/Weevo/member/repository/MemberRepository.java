package com.rudolph.Weevo.member.repository;

import com.rudolph.Weevo.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
