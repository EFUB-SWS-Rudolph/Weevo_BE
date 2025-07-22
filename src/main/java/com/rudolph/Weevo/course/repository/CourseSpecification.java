package com.rudolph.Weevo.course.repository;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.dto.request.CourseSearchRequest;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class CourseSpecification {

    public static Specification<Course> search(CourseSearchRequest req) {
        return (Root<Course> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            if (Course.class.equals(query.getResultType())) {
                root.fetch("teacher", JoinType.LEFT);
                query.distinct(true);
            }

            Predicate p = cb.conjunction();

            // 1) 타입·도시·카테고리 필터
            if (req.getCourseType() != null)
                p = cb.and(p, cb.equal(root.get("courseType"), req.getCourseType()));

            if (req.getCourseCity() != null)
                p = cb.and(p, cb.equal(root.get("courseCity"), req.getCourseCity()));

            if (req.getCourseCategory() != null)
                p = cb.and(p, cb.equal(root.get("courseCategory"), req.getCourseCategory()));

            // 2) 키워드 필터 (제목, 설명)
            if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
                String pattern = "%" + req.getKeyword().toLowerCase() + "%";

                Predicate titleLike = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descLike  = cb.like(cb.lower(root.get("description")), pattern);

                // title 또는 description 중 하나라도 매치해야 함
                p = cb.and(p, cb.or(titleLike, descLike));
            }

            // 3) 날짜 범위
            if (req.getCourseStartDate() != null)
                p = cb.and(p, cb.greaterThanOrEqualTo(
                        root.get("courseStartDate"), req.getCourseStartDate()));

            if (req.getCourseEndDate() != null)
                p = cb.and(p, cb.lessThanOrEqualTo(
                        root.get("courseEndDate"), req.getCourseEndDate()));

            return p;
        };
    }


}

