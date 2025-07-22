package com.rudolph.Weevo.course.repository;

import com.rudolph.Weevo.temp.domain.Member;
import com.rudolph.Weevo.course.domain.MemberCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberCourseRepository extends JpaRepository<MemberCourse, Long> {
    List<MemberCourse> findAllByMember(Member member);
}
