package com.rudolph.Weevo.member.repository;

import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.domain.MemberInterestTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberInterestTagRepository extends JpaRepository<MemberInterestTag, Long> {
    void deleteByMember(Member member);

    List<MemberInterestTag> findAllByMember(Member member);
}
