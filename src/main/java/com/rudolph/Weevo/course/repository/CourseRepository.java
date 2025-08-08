package com.rudolph.Weevo.course.repository;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.enums.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    List<Course> findAllByCourseCategoryIn(List<CourseCategory> categories);
    List<Course> findByTitleContaining(String keyword);
}
