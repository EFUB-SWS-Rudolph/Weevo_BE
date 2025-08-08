package com.rudolph.Weevo.member.repository;

import com.rudolph.Weevo.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByProviderId(String providerId);
    //회원 ID로 조회
    Optional<Member> findById(Long id);
    List<Member> findByNickNameContainingIgnoreCase(String keyword);
    List<Member> findByDepartmentContainingIgnoreCase(String keyword);

}
