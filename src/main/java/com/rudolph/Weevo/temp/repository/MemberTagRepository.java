package com.rudolph.Weevo.temp.repository;

import com.rudolph.Weevo.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberTagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findById(Long tagId);
}
