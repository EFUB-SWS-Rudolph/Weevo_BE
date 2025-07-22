package com.rudolph.Weevo.course.dto.response;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.CourseImage;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailResponse {

    private Long   courseId;
    private String courseTitle;
    private String courseType;
    private String courseCategory;
    private String courseCity;
    private Period period;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Period {
        private LocalDate start;
        private LocalDate end;
    }

    private String description;
    private Teacher teacher;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Teacher {
        private Long   id;
        private String nickname;
        private String college;
        private String department;
    }

    private List<String> images;
    private long   bookmarkCount;
    private boolean isBookmarked;

    public static CourseDetailResponse of(Course course,
                                          long bookmarkCnt,
                                          boolean myBookmark) {

        List<String> imageUrls = course.getCourseImages().stream()
                .map(CourseImage::getCourseImgUrl)
                .toList();

        return CourseDetailResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .courseType(course.getCourseType().name())
                .courseCategory(course.getCourseCategory().name())
                .courseCity(course.getCourseCity().name())
                .period(Period.builder()
                        .start(course.getCourseStartDate())
                        .end(course.getCourseEndDate())
                        .build())
                .description(course.getDescription())
                .teacher(Teacher.builder()
                        .id(course.getTeacher().getId())
                        .nickname(course.getTeacher().getNickName())
                        .college(course.getTeacher().getCollege())
                        .department(course.getTeacher().getDepartment())
                        .build())
                .images(imageUrls)
                .bookmarkCount(bookmarkCnt)
                .isBookmarked(myBookmark)
                .build();
    }
}
