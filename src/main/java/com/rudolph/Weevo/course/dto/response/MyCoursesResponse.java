package com.rudolph.Weevo.course.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyCoursesResponse {
    private List<CourseSummaryResponse> teachingCourses;
    private List<CourseSummaryResponse> enrolledCourses;
}