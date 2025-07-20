package com.rudolph.Weevo.course.service;

import com.rudolph.Weevo.course.dto.request.CreateCourseRequest;
import com.rudolph.Weevo.course.dto.response.CourseResponse;
import com.rudolph.Weevo.course.domain.*;
import com.rudolph.Weevo.course.repository.CourseBookmarkRepository;
import com.rudolph.Weevo.course.repository.CourseRepository;
import com.rudolph.Weevo.Member.domain.Member;
import com.rudolph.Weevo.Member.repository.MemberRepository;
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
public class CourseService {

    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final CourseBookmarkRepository bookmarkRepository;

    private final S3Service s3Service;

    // 1) 강의 생성
    @Transactional
    public CourseResponse createCourse(CreateCourseRequest req, UUID teacherUuid) {
        // 강사 조회
        Member teacher = memberRepository.findByMemberId(teacherUuid)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));

        // Course 저장
        Course course = new Course(
                null,
                req.getCourseTitle(),
                req.getDescription(),
                req.getCourseStartDate(),
                req.getCourseEndDate(),
                req.getCourseType(),
                req.getCourseCategory(),
                req.getCourseCity(),
                teacher,
                new ArrayList<>(),
                new ArrayList<>()
        );
        course = courseRepository.saveAndFlush(course);   // createdAt/updatedAt 즉시 채움

        // 이미지 업로드
        List<String> urls = new ArrayList<>();
        if (req.getImages() != null) {
            for (int i = 0; i < req.getImages().size(); i++) {
                MultipartFile file = req.getImages().get(i);
                String url = s3Service.uploadFile(file, "courses/" + course.getId());
                CourseImage img = CourseImage.builder()
                        .courseImgUrl(url)
                        .isThumbnail(i == 0)
                        .course(course)
                        .build();
                course.getCourseImages().add(img);
                urls.add(url);
            }
        }

        // 응답 DTO
        return CourseResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .description(course.getDescription())
                .courseStartDate(course.getCourseStartDate().toString())
                .courseEndDate(course.getCourseEndDate().toString())
                .courseType(course.getCourseType().name())
                .courseCategory(course.getCourseCategory().name())
                .courseCity(course.getCourseCity().name())
                .teacherId(teacher.getId())
                .images(urls)
                .createdAt(course.getCreatedAt().toString())
                .updatedAt(course.getUpdatedAt().toString())
                .build();
    }

    // 2) 강의 찜하기
    @Transactional
    public void addBookmark(UUID memberUuid, Long courseId) {

        // 1) 회원 찾기 (UUID → Member)
        Member member = memberRepository.findByMemberId(memberUuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 2) 강의 찾기
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COURSE_NOT_FOUND));

        // 3) 중복 체크
        if (bookmarkRepository.existsByMemberIdAndCourseId(member.getId(), courseId)) return;

        // 4) 저장
        bookmarkRepository.save(
                CourseBookmark.builder()
                        .member(member)
                        .course(course)
                        .build()
        );
    }

    // 3) 찜 취소하기
    @Transactional
    public void removeBookmark(UUID memberUuid, Long courseId) {

        Member member = memberRepository.findByMemberId(memberUuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        bookmarkRepository.deleteByMemberIdAndCourseId(member.getId(), courseId);
    }
}
