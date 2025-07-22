package com.rudolph.Weevo.member.repository;

import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.domain.MemberInterestTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberInterestTagRepository extends JpaRepository<MemberInterestTag, Long> {
    void deleteByMember(Member member);
}
