package com.rudolph.Weevo.course.service;

import com.rudolph.Weevo.course.domain.enums.CourseCategory;
import com.rudolph.Weevo.course.dto.request.CourseSearchRequest;
import com.rudolph.Weevo.course.dto.request.CreateCourseRequest;
import com.rudolph.Weevo.course.dto.response.*;
import com.rudolph.Weevo.course.domain.*;
import com.rudolph.Weevo.course.repository.CourseBookmarkRepository;
import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.MemberCourse;
import com.rudolph.Weevo.course.repository.CourseRepository;
import com.rudolph.Weevo.course.repository.CourseSpecification;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.global.service.S3Service;
import com.rudolph.Weevo.course.repository.MemberCourseRepository;
import com.rudolph.Weevo.member.service.MemberService;
import com.rudolph.Weevo.notification.domain.enums.NotiType;
import com.rudolph.Weevo.notification.service.NotificationService;
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

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final CourseBookmarkRepository bookmarkRepository;
    private final MemberCourseRepository memberCourseRepository;
    private final S3Service s3Service;
    private final NotificationService notificationService;
    private final MemberService memberService;

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
        Member student = memberService.findMember(studentId);
        //진행자 조회
        Member teacher = memberService.findMember(teacherId);

        // 중복 확인
        boolean exists = course.getMemberCourses().stream()
                .anyMatch(mc -> mc.getMember().getId().equals(studentId));
        if (exists) throw new GeneralException(ErrorStatus.COURSE_ALREADY_CONFIRMED);

        MemberCourse mc = MemberCourse.of(student, course);
        course.getMemberCourses().add(mc);

        courseRepository.save(course);
        notificationService.createNotification(NotiType.COURSE_MATCHED, teacher, student, course.getTitle());
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
        
    // 9) 강의 성사 취소
    @Transactional
    public String cancelCourse(Long courseId,
                               Long studentId,
                               Long loginId) {

        // 0. 강의 존재 확인
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COURSE_NOT_FOUND));

        boolean isTeacher = course.getTeacher().getId().equals(loginId);

        // 1. 권한 체크
        if (!isTeacher && !loginId.equals(studentId)) {
            // 학생이 다른 사람 enrollment 를 취소하려 함
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 2) (course, student) 한 행 조회
        MemberCourse mc = memberCourseRepository
                .findByCourseIdAndMemberId(courseId, studentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBERCOURSE_NOT_FOUND));

        // 3. 도메인 로직 실행
        var result = mc.applyCancelRequest(isTeacher);

        String response = switch (result) {
            case DUPLICATE -> throw new GeneralException(ErrorStatus.COURSE_CANCEL_ALREADY_REQUESTED);

            case BOTH -> {
                memberCourseRepository.delete(mc);
                yield "성사 취소 완료되었습니다.";
            }

            case PENDING -> "성사 취소 신청 완료되었습니다. 상대방도 성사 취소 버튼을 눌러야 최종 취소됩니다.";
        };

        Member sender = memberService.findMember(loginId);
        Member student = memberService.findMember(studentId);
        if (result.equals(MemberCourse.CancelResult.PENDING) && isTeacher){
            notificationService.createNotification(NotiType.COURSE_CANCELED, sender, student, course.getTitle());
        } else {
            notificationService.createNotification(NotiType.COURSE_CANCELED, sender, course.getTeacher(), course.getTitle());
        }

        return response;
    }

    // 10) 내가 찜한 강의 조회
    @Transactional(readOnly = true)
    public MyCourseListDto getMyBookmarkCourses(Long memberId) {

        //찜한 강의 목록
        List<Course> bookmarkedCourses = bookmarkRepository.findBookmarkedCoursesByMemberId(memberId);
        return MyCourseListDto.from(bookmarkedCourses);
    }

    // 11) 추천 강의 조회
    @Transactional(readOnly = true)
    public PagedCourseResponse getRecommend(Long memberId, Pageable pageable) {
        // 1) 회원과 관심 태그 조회
        Member member = memberService.findMember(memberId);
        List<CourseCategory> categories = member.getInterestTags().stream()
                .map(it -> it.getTag().getCategory())
                .distinct()
                .toList();

        // 2) 관심 카테고리가 없으면 빈 페이지 리턴
        if (categories.isEmpty()) {
            return PagedCourseResponse.builder()
                    .pageNumber(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalElements(0)
                    .totalPages(0)
                    .courses(Collections.emptyList())
                    .build();
        }

        // 3) 해당 카테고리 강의 전체 조회
        List<Course> candidates = courseRepository.findAllByCourseCategoryIn(categories);

        // 4) 찜(북마크) 수 집계
        List<Long> courseIds = candidates.stream()
                .map(Course::getId)
                .toList();
        Map<Long, Long> bookmarkCountMap = bookmarkRepository.countByCourseIds(courseIds);

        // 5) 썸네일 URL 맵 생성
        Map<Long, String> thumbnailMap = candidates.stream()
                .collect(Collectors.toMap(
                        Course::getId,
                        c -> c.getCourseImages().stream()
                                .filter(CourseImage::isThumbnail)
                                .findFirst()
                                .map(CourseImage::getCourseImgUrl)
                                .orElse("")
                ));

        // 6) DTO 변환 후 인기순 정렬
        List<CourseListResponse> sorted = candidates.stream()
                .map(c -> CourseListResponse.of(
                        c,
                        bookmarkCountMap.getOrDefault(c.getId(), 0L),
                        thumbnailMap.get(c.getId())
                ))
                .sorted(Comparator.comparingLong(CourseListResponse::getBookmarkCount).reversed())
                .toList();

        // 7) 수동 페이징
        int total = sorted.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<CourseListResponse> pageItems = start >= end
                ? Collections.emptyList()
                : sorted.subList(start, end);

        int totalPages = (int) Math.ceil((double) total / pageable.getPageSize());

        // 8) 응답 빌드
        return PagedCourseResponse.builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(total)
                .totalPages(totalPages)
                .courses(pageItems)
                .build();
    }

}


