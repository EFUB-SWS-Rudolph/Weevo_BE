package com.rudolph.Weevo.member.service;

import com.rudolph.Weevo.course.dto.response.CourseListResponse;
import com.rudolph.Weevo.course.dto.response.CourseResponse;
import com.rudolph.Weevo.course.repository.CourseRepository;
import com.rudolph.Weevo.member.dto.response.MemberListResponse;
import com.rudolph.Weevo.member.dto.response.SearchResponseDto;
import com.rudolph.Weevo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    public SearchResponseDto searchAll(String keyword, Long id){
        // 1. 이름(닉네임) 기준 이화인 검색
        List<MemberListResponse> membersByName = memberRepository.findByNickNameContaining(keyword)
                .stream()
                .map(MemberListResponse::from)
                .toList();

        // 2. 학과 기준 이화인 검색
        List<MemberListResponse> membersByDepartment = memberRepository.findByDepartmentContaining(keyword)
                .stream()
                .map(MemberListResponse::from)
                .toList();

        // 3. 강의명 기준 강의 검색
//        List<CourseListResponse> courses = courseRepository.findByTitleContaining(keyword)
//                .stream()
//                .map(c -> CourseListResponse.of(c, 0L, c.getThumbnailUrl()))
//                .toList();

        return SearchResponseDto.builder()
                .memberList(membersByName)
                .departmentList(membersByDepartment)
//                .courseList(courses)
                .build();
    }
}
