package com.rudolph.Weevo.member.repository;

import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.domain.MemberTalentTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTalentTagRepository extends JpaRepository<MemberTalentTag, Long> {
    void deleteByMember(Member member);
}
