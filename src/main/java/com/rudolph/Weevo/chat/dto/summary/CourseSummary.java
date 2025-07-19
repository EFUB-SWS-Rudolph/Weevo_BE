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
                .recruitStartDate(course.getRecruitStartDate().toString())
                .recruitEndDate(course.getRecruitEndDate().toString())
                .build();
    }
}
