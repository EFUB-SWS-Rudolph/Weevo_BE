package com.rudolph.Weevo.course.controller;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.course.dto.response.MyCourseListDto;
import com.rudolph.Weevo.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/my-course")   //내 강의 조회
    public ResponseEntity<MyCourseListDto> getMyCourses(@AuthenticationPrincipal CustomUserPrincipal principal) {
        MyCourseListDto myCourseList = courseService.getMyCourseList(principal);
        return ResponseEntity.ok(myCourseList);
    }

}
