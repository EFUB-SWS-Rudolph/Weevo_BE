package com.rudolph.Weevo.course.dto.response;

import com.rudolph.Weevo.course.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class MyCourseListDto {
    private List<MyCourseDto> myCourses;

    public static MyCourseListDto from(List<Course> courseList) {
        //찜한 강의가 없을 경우 빈 배열 반환
        if (courseList == null || courseList.isEmpty()) {
            return new MyCourseListDto(Collections.emptyList());
        }

        List<MyCourseDto> myCourses = courseList.stream()
                .map(MyCourseDto::from)
                .toList();
        return new MyCourseListDto(myCourses);
    }
}
