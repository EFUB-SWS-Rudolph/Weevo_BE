package com.rudolph.Weevo.course.service;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.MemberCourse;
import com.rudolph.Weevo.course.dto.response.MyCourseListDto;
import com.rudolph.Weevo.course.repository.CourseRepository;
import com.rudolph.Weevo.course.repository.MemberCourseRepository;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final MemberCourseRepository memberCourseRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Course findCourse(Long userId) {
        return courseRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COURSE_NOT_FOUND));
    }

    @Transactional(readOnly = true)     //내 강의 조회
    public MyCourseListDto getMyCourseList(CustomUserPrincipal principal) {
        UUID memberId = principal.getMemberId();
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        List<MemberCourse> memberCourses = memberCourseRepository.findAllByMember(member);
        List<Course> myCourses = memberCourses.stream()
                .map(MemberCourse::getCourse)
                .toList();
        return MyCourseListDto.from(myCourses);
    }
}
