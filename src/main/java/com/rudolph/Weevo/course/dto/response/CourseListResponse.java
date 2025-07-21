package com.rudolph.Weevo.course.dto.response;

import com.rudolph.Weevo.course.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CourseListResponse {

    private Long courseId;
    private String courseTitle;
    private Long teacherId;
    private String teacherNickname;
    private String courseStartDate;
    private String courseEndDate;
    private String courseType;
    private String courseCategory;
    private String courseCity;
    private String thumbnailUrl;
    private long bookmarkCount;

    public static CourseListResponse of(Course c, long bookmarkCnt, String thumbnail) {
        return CourseListResponse.builder()
                .courseId(c.getId())
                .courseTitle(c.getTitle())
                .teacherId(c.getTeacher().getId())
                .teacherNickname(c.getTeacher().getNickName())
                .courseStartDate(c.getCourseStartDate().toString())
                .courseEndDate(c.getCourseEndDate().toString())
                .courseType(c.getCourseType().name())
                .courseCategory(c.getCourseCategory().name())
                .courseCity(c.getCourseCity().name())
                .thumbnailUrl(thumbnail)
                .bookmarkCount(bookmarkCnt)
                .build();
    }
}