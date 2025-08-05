package com.rudolph.Weevo.course.dto.response;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.CourseImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MyCourseDto {
    private Long courseId;
    private String title;
    private String description;
    private LocalDate courseStartDate;
    private String teacher;
    private String thumbnailUrl;


    public static MyCourseDto from(Course course) {
        String thumbnailUrl = course.getCourseImages().stream()
                .filter(CourseImage::isThumbnail)   //썸네일로 지정된 것만 필터링
                .findFirst()
                .map(CourseImage::getCourseImgUrl)
                .orElse(null);
        return new MyCourseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCourseStartDate(),
                course.getTeacher().getNickName(),
                thumbnailUrl
        );
    }
}
