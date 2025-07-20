package com.rudolph.Weevo.course.dto.response;

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
}
