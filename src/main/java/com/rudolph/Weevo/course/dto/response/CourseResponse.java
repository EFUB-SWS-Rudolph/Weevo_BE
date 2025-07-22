package com.rudolph.Weevo.course.dto.response;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.CourseImage;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {

    private Long courseId;
    private String courseTitle;
    private String description;
    private String courseStartDate;
    private String courseEndDate;
    private String courseType;
    private String courseCategory;
    private String courseCity;
    private Long teacherId;
    private List<String> images;
    private String createdAt;
    private String updatedAt;

    public static CourseResponse from(Course course) {
        List<String> imageUrls = course.getCourseImages().stream()
                .map(CourseImage::getCourseImgUrl)
                .toList();

        return CourseResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .description(course.getDescription())
                .courseStartDate(course.getCourseStartDate().toString())
                .courseEndDate(course.getCourseEndDate().toString())
                .courseType(course.getCourseType().name())
                .courseCategory(course.getCourseCategory().name())
                .courseCity(course.getCourseCity().name())
                .teacherId(course.getTeacher().getId())
                .images(imageUrls)
                .createdAt(course.getCreatedAt().toString())
                .updatedAt(course.getUpdatedAt().toString())
                .build();
    }
}
