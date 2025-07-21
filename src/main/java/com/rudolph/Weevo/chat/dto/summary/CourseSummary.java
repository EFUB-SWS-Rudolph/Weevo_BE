package com.rudolph.Weevo.chat.dto.summary;

import com.rudolph.Weevo.course.domain.Course;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseSummary {
    private Long courseId;
    private String title;
    private String recruitStartDate;
    private String recruitEndDate;

    public static CourseSummary from(Course course) {
        return CourseSummary.builder()
                .courseId(course.getId())
                .title(course.getTitle())
                .recruitStartDate(course.getCourseStartDate().toString())
                .recruitEndDate(course.getCourseEndDate().toString())
                .build();
    }
}
