package com.rudolph.Weevo.member.repository;

import com.rudolph.Weevo.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    @Query("SELECT m FROM Member m WHERE m.memberId = :memberId")
    Optional<Member> findByMemberId(UUID memberId);

    Member findByProviderId(String providerId);

    //회원 ID로 조회
    Optional<Member> findById(Long id);

}
