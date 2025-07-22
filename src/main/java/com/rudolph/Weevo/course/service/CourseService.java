package com.rudolph.Weevo.course.service;

import com.rudolph.Weevo.course.dto.request.CourseSearchRequest;
import com.rudolph.Weevo.course.dto.request.CreateCourseRequest;
import com.rudolph.Weevo.course.dto.response.*;
import com.rudolph.Weevo.course.domain.*;
import com.rudolph.Weevo.course.repository.CourseBookmarkRepository;
import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.MemberCourse;
import com.rudolph.Weevo.course.dto.response.MyCourseListDto;
import com.rudolph.Weevo.course.repository.CourseRepository;
import com.rudolph.Weevo.course.repository.CourseSpecification;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.global.service.S3Service;
import com.rudolph.Weevo.course.repository.MemberCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final CourseBookmarkRepository bookmarkRepository;
    private final MemberCourseRepository memberCourseRepository;
    private final S3Service s3Service;

    // 1) 강의 생성
    public CourseResponse createCourse(CreateCourseRequest req, Long teacherId) {

        Member teacher = memberRepository.findById(teacherId)
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

        return CourseResponse.from(course);
    }

    // 2) 강의 찜하기
    public void addBookmark(Long memberId, Long courseId) {

        Member member = memberRepository.findById(memberId)
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
    public void removeBookmark(Long memberId, Long courseId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        bookmarkRepository.deleteByMemberIdAndCourseId(member.getId(), courseId);
    }

    // 4) 단건 조회
    @Transactional(readOnly = true)
    public Course findCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COURSE_NOT_FOUND));
    }

    // 5) 강의 목록 조회
    public PagedCourseResponse listCourses(CourseSearchRequest req, Pageable pageable) {

        if (req.getSort() != null &&
                !req.getSort().equalsIgnoreCase("latest") &&
                !req.getSort().equalsIgnoreCase("popular")) {
            throw new GeneralException(ErrorStatus.INVALID_SORT);
        }

        Pageable pageWithSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("latest".equalsIgnoreCase(req.getSort()) || req.getSort() == null
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC,
                        "createdAt")
        );

        Page<Course> page = courseRepository.findAll(
                CourseSpecification.search(req), pageWithSort);

        List<Long> ids = page.getContent().stream().map(Course::getId).toList();
        Map<Long, Long> bmCnt = bookmarkRepository.countByCourseIds(ids);

        Map<Long, String> thumb = page.getContent().stream()
                .collect(Collectors.toMap(
                        Course::getId,
                        c -> c.getCourseImages().stream()
                                .filter(CourseImage::isThumbnail)
                                .findFirst()
                                .map(CourseImage::getCourseImgUrl)
                                .orElse("")
                ));


        List<CourseListResponse> list = page.getContent().stream()
                .map(c -> CourseListResponse.of(
                        c,
                        bmCnt.getOrDefault(c.getId(), 0L),
                        thumb.get(c.getId())
                ))
                .collect(Collectors.toCollection(ArrayList::new));

        if ("popular".equalsIgnoreCase(req.getSort())) {
            list.sort(Comparator.comparingLong(CourseListResponse::getBookmarkCount).reversed());
        }

        return PagedCourseResponse.builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .courses(list)
                .build();
    }

    // 6) 강의 상세 조회
    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseDetail(Long courseId, Long memberId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COURSE_NOT_FOUND));

        long bookmarkCnt = bookmarkRepository.countByCourseId(courseId);

        // 로그인 사용자가 강의를 찜했는지 여부
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        boolean myBookmark = bookmarkRepository
                .existsByMemberIdAndCourseId(member.getId(), courseId);

        return CourseDetailResponse.of(course, bookmarkCnt, myBookmark);
    }

    // 7) 강의 성사
    @Transactional
    public void confirmCourse(Long courseId, Long teacherId, Long studentId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COURSE_NOT_FOUND));

        // 진행자 권한 검증
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_COURSE_CONFIRM);
        }

        // 수강자 조회
        Member student = memberRepository.findById(studentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 중복 확인
        boolean exists = course.getMemberCourses().stream()
                .anyMatch(mc -> mc.getMember().getId().equals(studentId));
        if (exists) throw new GeneralException(ErrorStatus.COURSE_ALREADY_CONFIRMED);

        MemberCourse mc = MemberCourse.of(student, course);
        course.getMemberCourses().add(mc);

        courseRepository.save(course);
    }

    // 8) 내 강의 조회
    @Transactional(readOnly = true)
    public MyCoursesResponse listMyCourses(Long memberId) {

        Member me = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        List<CourseSummaryResponse> teaching = me.getCourses().stream()
                .map(CourseSummaryResponse::of)
                .toList();

        List<CourseSummaryResponse> enrolled = me.getMemberCourses().stream()
                .map(MemberCourse::getCourse)
                .map(CourseSummaryResponse::of)
                .toList();

        return MyCoursesResponse.builder()
                .teachingCourses(teaching)
                .enrolledCourses(enrolled)
                .build();
    }
  
//     @Transactional(readOnly = true)     //내 강의 조회
//     public MyCourseListDto getMyCourseList(CustomUserPrincipal principal) {
//         UUID memberId = principal.getMemberId();
//         Member member = memberRepository.findByMemberId(memberId)
//                 .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
//         List<MemberCourse> memberCourses = memberCourseRepository.findAllByMember(member);
//         List<Course> myCourses = memberCourses.stream()
//                 .map(MemberCourse::getCourse)
//                 .toList();
//         return MyCourseListDto.from(myCourses);

}


