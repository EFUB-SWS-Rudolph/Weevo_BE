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

            if (req.getCourseType() != null)
                p = cb.and(p, cb.equal(root.get("courseType"), req.getCourseType()));

            if (req.getCourseCity() != null)
                p = cb.and(p, cb.equal(root.get("courseCity"), req.getCourseCity()));

            if (req.getCourseCategory() != null)
                p = cb.and(p, cb.equal(root.get("courseCategory"), req.getCourseCategory()));

            if (req.getCourseStartDate() != null)
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get("courseStartDate"), req.getCourseStartDate()));

            if (req.getCourseEndDate() != null)
                p = cb.and(p, cb.lessThanOrEqualTo(root.get("courseEndDate"), req.getCourseEndDate()));

            return p;
        };
    }
}

