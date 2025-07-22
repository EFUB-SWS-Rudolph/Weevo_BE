package com.rudolph.Weevo.course.dto.request;

import com.rudolph.Weevo.course.domain.enums.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CourseSearchRequest {

    private String sort = "latest";   // latest | popular

    private CourseType courseType;
    private CourseCity courseCity;
    private CourseCategory courseCategory;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate courseStartDate;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate courseEndDate;
}
