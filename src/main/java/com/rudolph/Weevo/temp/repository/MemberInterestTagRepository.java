package com.rudolph.Weevo.temp.repository;

import com.rudolph.Weevo.temp.domain.Member;
import com.rudolph.Weevo.temp.domain.MemberInterestTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberInterestTagRepository extends JpaRepository<MemberInterestTag, Long> {
    void deleteByMember(Member member);
}
