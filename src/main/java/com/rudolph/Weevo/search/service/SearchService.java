package com.rudolph.Weevo.search.service;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.CourseImage;
import com.rudolph.Weevo.course.dto.response.CourseListResponse;
import com.rudolph.Weevo.course.repository.CourseBookmarkRepository;
import com.rudolph.Weevo.course.repository.CourseRepository;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.dto.response.MemberListResponse;
import com.rudolph.Weevo.member.repository.MemberRepository;
import com.rudolph.Weevo.search.dto.response.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private static final int MAX_KEYWORD_LENGTH = 50;

    private final CourseRepository courseRepo;
    private final MemberRepository memberRepo;
    private final CourseBookmarkRepository bookmarkRepo;

    public SearchResponse search(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.KEYWORD_REQUIRED);
        }
        keyword = keyword.trim();
        if (keyword.length() > MAX_KEYWORD_LENGTH) {
            throw new GeneralException(ErrorStatus.KEYWORD_TOO_LONG);
        }

        List<Course> courses = courseRepo.findByTitleContainingIgnoreCase(keyword);

        Map<Long, Long> bmCnt = bookmarkRepo.countByCourseIds(
                courses.stream().map(Course::getId).toList());

        Map<Long, String> thumb = courses.stream()
                .collect(Collectors.toMap(
                        Course::getId,
                        c -> c.getCourseImages().stream()
                                .filter(CourseImage::isThumbnail)
                                .findFirst()
                                .map(CourseImage::getCourseImgUrl)
                                .orElse("")
                ));

        List<CourseListResponse> courseDtos = courses.stream()
                .map(c -> CourseListResponse.of(
                        c,
                        bmCnt.getOrDefault(c.getId(), 0L),
                        thumb.get(c.getId())
                ))
                .toList();

        List<Member> nickMembers = memberRepo.findByNickNameContainingIgnoreCase(keyword);
        List<Member> deptMembers = memberRepo.findByDepartmentContainingIgnoreCase(keyword);

        List<MemberListResponse> nickDtos = nickMembers.stream()
                .map(MemberListResponse::from)
                .toList();
        List<MemberListResponse> deptDtos = deptMembers.stream()
                .map(MemberListResponse::from)
                .toList();

        return SearchResponse.builder()
                .courses(courseDtos)
                .membersByNickname(nickDtos)
                .membersByDept(deptDtos)
                .build();
    }
}
