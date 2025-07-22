package com.rudolph.Weevo.auth.repository;

import com.rudolph.Weevo.auth.domain.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByMemberId(Long memberId);

    @Transactional
    @Modifying
    void deleteByMemberId(Long memberId);
}
