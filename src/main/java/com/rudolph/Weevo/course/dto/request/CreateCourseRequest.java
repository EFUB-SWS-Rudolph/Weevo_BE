package com.rudolph.Weevo.course.dto.request;

import com.rudolph.Weevo.course.domain.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {

    @NotBlank
    private String courseTitle;

    private String description;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate courseStartDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate courseEndDate;

    @NotNull
    private CourseType courseType;

    @NotNull
    private CourseCategory courseCategory;

    @NotNull
    private CourseCity courseCity;

    private List<MultipartFile> images;
}
