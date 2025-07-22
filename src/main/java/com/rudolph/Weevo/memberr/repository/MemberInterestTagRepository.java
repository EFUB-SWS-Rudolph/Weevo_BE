package com.rudolph.Weevo.memberr.repository;

import com.rudolph.Weevo.memberr.domain.Member;
import com.rudolph.Weevo.memberr.domain.MemberInterestTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberInterestTagRepository extends JpaRepository<MemberInterestTag, Long> {
    void deleteByMember(Member member);
}
