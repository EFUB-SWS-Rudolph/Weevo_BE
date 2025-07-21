package com.rudolph.Weevo.course.repository;

import com.rudolph.Weevo.course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
