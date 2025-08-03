package com.rudolph.Weevo.tag.repository;

import com.rudolph.Weevo.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByNameIn(List<String> names);
    Optional<Tag> findByName(String name);
}
