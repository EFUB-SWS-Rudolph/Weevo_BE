package com.rudolph.Weevo.course.service;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.repository.CourseRepository;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public Course findCourse(Long userId) {
        return courseRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COURSE_NOT_FOUND));
    }
}
