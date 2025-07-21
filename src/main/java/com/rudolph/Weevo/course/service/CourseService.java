package com.rudolph.Weevo.course.service;

import com.rudolph.Weevo.course.dto.request.CreateCourseRequest;
import com.rudolph.Weevo.course.dto.response.CourseResponse;
import com.rudolph.Weevo.course.domain.*;
import com.rudolph.Weevo.course.repository.CourseBookmarkRepository;
import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.repository.CourseRepository;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final CourseBookmarkRepository bookmarkRepository;
    private final S3Service s3Service;

    // 1) 강의 생성
    public CourseResponse createCourse(CreateCourseRequest req, UUID teacherUuid) {

        Member teacher = memberRepository.findByMemberId(teacherUuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Course course = Course.builder()
                .title(req.getCourseTitle())
                .description(req.getDescription())
                .courseStartDate(req.getCourseStartDate())
                .courseEndDate(req.getCourseEndDate())
                .courseType(req.getCourseType())
                .courseCategory(req.getCourseCategory())
                .courseCity(req.getCourseCity())
                .teacher(teacher)
                .build();

        courseRepository.save(course);

        // 이미지 업로드
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            int idx = 0;
            for (MultipartFile file : req.getImages()) {
                String url = s3Service.uploadFile(file, "courses/" + course.getId());
                CourseImage img = CourseImage.builder()
                        .courseImgUrl(url)
                        .isThumbnail(idx++ == 0)
                        .course(course)
                        .build();
                course.getCourseImages().add(img);
            }
        }

        return CourseResponse.from(course);   // ⬅️ 아래 2번 참고
    }

    // 2) 강의 찜하기
    public void addBookmark(UUID memberUuid, Long courseId) {

        Member member = memberRepository.findByMemberId(memberUuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COURSE_NOT_FOUND));

        if (bookmarkRepository.existsByMemberIdAndCourseId(member.getId(), courseId)) return;

        bookmarkRepository.save(
                CourseBookmark.builder()
                        .member(member)
                        .course(course)
                        .build()
        );
    }

    // 3) 찜 취소
    public void removeBookmark(UUID memberUuid, Long courseId) {
        Member member = memberRepository.findByMemberId(memberUuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        bookmarkRepository.deleteByMemberIdAndCourseId(member.getId(), courseId);
    }

    // 4) 단건 조회
    @Transactional(readOnly = true)
    public Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COURSE_NOT_FOUND));
    }
}


