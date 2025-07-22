package com.rudolph.Weevo.course.repository;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.CourseBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Long> {

    // 1) 특정 강의 찜 수
    @Query("select count(b) from CourseBookmark b where b.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    // 2) 사용자가 이미 해당 강의를 찜했는지 확인
    boolean existsByMemberIdAndCourseId(Long memberId, Long courseId);

    // 3) 찜 취소
    void deleteByMemberIdAndCourseId(Long memberId, Long courseId);

    // 4) 북마크 수 조회
    @Query("select b.course.id, count(b) from CourseBookmark b where b.course.id in :ids group by b.course.id")
    List<Object[]> findBookmarkCountIn(@Param("ids") List<Long> ids);

    // 5) 북마크 수에 따른 강의 목록 조회
    default Map<Long, Long> countByCourseIds(List<Long> ids) {
        return findBookmarkCountIn(ids).stream()
                .collect(Collectors.toMap(t -> (Long) t[0], t -> (Long) t[1]));
    }

    // 6) 사용자가 북마크한 강의 목록 조회
    List<Course> findAllBookmarkedByMemberId(Long memberId);
}