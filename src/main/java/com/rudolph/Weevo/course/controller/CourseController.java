package com.rudolph.Weevo.course.controller;

import com.rudolph.Weevo.course.dto.response.CourseDetailResponse;
import com.rudolph.Weevo.course.dto.request.CourseSearchRequest;
import com.rudolph.Weevo.course.dto.request.CreateCourseRequest;
import com.rudolph.Weevo.course.dto.response.CourseResponse;
import com.rudolph.Weevo.course.dto.response.MyCoursesResponse;
import com.rudolph.Weevo.course.dto.response.PagedCourseResponse;
import com.rudolph.Weevo.course.service.CourseService;
import com.rudolph.Weevo.global.common.api.ApiResponse;
import com.rudolph.Weevo.global.common.code.SuccessStatus;
import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.rudolph.Weevo.course.dto.response.MyCourseListDto;

import java.util.Map;

@Tag(name = "Course", description = "강의 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    // 1) 강의 생성
    @Operation(summary = "강의 생성", description = "새로운 강의를 등록합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @ModelAttribute @Valid CreateCourseRequest request,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        CourseResponse resp = courseService.createCourse(request, user.getMemberId());
        return ApiResponse.onSuccess(SuccessStatus._OK, resp);
    }

    // 2) 강의 찜하기
    @PostMapping("/bookmark/{courseId}")
    public ResponseEntity<ApiResponse<Void>> addBookmark(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserPrincipal user) {

        courseService.addBookmark(user.getMemberId(), courseId);
        return ApiResponse.onSuccess(SuccessStatus._OK, null);
    }

    // 3) 강의 찜 취소
    @DeleteMapping("/bookmark/{courseId}")
    public ResponseEntity<ApiResponse<Void>> removeBookmark(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserPrincipal user) {

        courseService.removeBookmark(user.getMemberId(), courseId);
        return ApiResponse.onSuccess(SuccessStatus._OK, null);
    }

    // 5) 강의 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<PagedCourseResponse>> getCourses(
            @ModelAttribute CourseSearchRequest req,
            Pageable pageable
    ) {
        PagedCourseResponse resp = courseService.listCourses(req, pageable);
        return ApiResponse.onSuccess(SuccessStatus._OK, resp);
    }

    // 6) 강의 상세 조회
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        CourseDetailResponse resp =
                courseService.getCourseDetail(courseId, user.getMemberId());
        return ApiResponse.onSuccess(SuccessStatus._OK, resp);
    }

    // 7) 강의 성사
    @PostMapping("/confirm/{courseId}/{memberId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> confirmCourse(
            @PathVariable Long courseId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        courseService.confirmCourse(courseId, user.getMemberId(), memberId);

        Map<String, Object> resp = Map.of(
                "courseId",   courseId,
                "studentId",  memberId,
                "confirmedAt", java.time.LocalDateTime.now()
        );
        return ApiResponse.onSuccess(SuccessStatus._OK, resp);
    }

    // 8) 내 강의 조회
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<MyCoursesResponse>> getMyCourses(
            @AuthenticationPrincipal CustomUserPrincipal user) {

        MyCoursesResponse resp = courseService.listMyCourses(user.getMemberId());
        return ApiResponse.onSuccess(SuccessStatus._OK, resp);
    }
  
  
//     @GetMapping("/my-course")   //내 강의 조회
//     public ResponseEntity<MyCourseListDto> getMyCourses(@AuthenticationPrincipal CustomUserPrincipal principal) {
//         MyCourseListDto myCourseList = courseService.getMyCourseList(principal);
//         return ResponseEntity.ok(myCourseList);
//     }


}

