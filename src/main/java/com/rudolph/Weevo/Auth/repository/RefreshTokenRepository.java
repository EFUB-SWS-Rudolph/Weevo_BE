package com.rudolph.Weevo.Auth.repository;

import com.rudolph.Weevo.Auth.domain.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT m FROM RefreshToken m WHERE m.memberId = :memberId")
    RefreshToken findByMemberId(UUID memberId);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken m WHERE m.memberId = memberId")
    void deleteByMemberId(UUID memberId);
}
