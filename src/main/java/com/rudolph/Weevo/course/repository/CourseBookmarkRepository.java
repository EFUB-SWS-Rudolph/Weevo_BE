package com.rudolph.Weevo.course.repository;

import com.rudolph.Weevo.course.domain.CourseBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Long> {

    // 1) 특정 강의 찜 수
    @Query("select count(b) from CourseBookmark b where b.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    // 2) 사용자가 이미 해당 강의를 찜했는지 확인
    boolean existsByMemberIdAndCourseId(Long memberId, Long courseId);

    // 3) 찜 취소
    void deleteByMemberIdAndCourseId(Long memberId, Long courseId);
}