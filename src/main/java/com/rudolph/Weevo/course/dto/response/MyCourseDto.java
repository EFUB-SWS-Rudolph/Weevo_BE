package com.rudolph.Weevo.course.dto.response;

import com.rudolph.Weevo.course.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCourseDto {
    private Long courseId;
    private String title;
    private String description;

    public static MyCourseDto from(Course course) {
        return new MyCourseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription()
        );
    }
}
