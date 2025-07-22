package com.rudolph.Weevo.course.dto.response;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.CourseImage;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseSummaryResponse {

    private Long   courseId;
    private String courseTitle;
    private String courseType;
    private String courseCity;
    private String description;
    private String courseStartDate;
    private String courseEndDate;
    private String courseCategory;
    private Long   teacherId;
    private String teacherNickname;
    private String thumbnailUrl;
    private String createdAt;
    private String updatedAt;

    public static CourseSummaryResponse of(Course c) {

        String thumb = c.getCourseImages().stream()
                .filter(CourseImage::isThumbnail)
                .findFirst()
                .map(CourseImage::getCourseImgUrl)
                .orElse("");

        return CourseSummaryResponse.builder()
                .courseId(c.getId())
                .courseTitle(c.getTitle())
                .courseType(c.getCourseType().name())
                .courseCity(c.getCourseCity().name())
                .description(c.getDescription())
                .courseStartDate(c.getCourseStartDate().toString())
                .courseEndDate(c.getCourseEndDate().toString())
                .courseCategory(c.getCourseCategory().name())
                .teacherId(c.getTeacher().getId())
                .teacherNickname(c.getTeacher().getNickName())
                .thumbnailUrl(thumb)
                .createdAt(c.getCreatedAt().toString())
                .updatedAt(c.getUpdatedAt().toString())
                .build();
    }
}