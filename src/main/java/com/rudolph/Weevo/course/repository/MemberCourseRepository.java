package com.rudolph.Weevo.course.repository;

import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.course.domain.MemberCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCourseRepository extends JpaRepository<MemberCourse, Long> {
    List<MemberCourse> findAllByMember(Member member);

    Optional<MemberCourse> findByCourseIdAndMemberId(Long courseId, Long memberId);

    boolean existsByCourseIdAndMemberId(Long courseId, Long memberId);
}
