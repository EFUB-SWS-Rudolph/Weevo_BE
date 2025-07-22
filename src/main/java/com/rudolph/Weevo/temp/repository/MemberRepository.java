package com.rudolph.Weevo.temp.repository;

import com.rudolph.Weevo.temp.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Member findByProviderId(String providerId);

    //회원 ID로 조회
    Optional<Member> findById(Long id);

}
