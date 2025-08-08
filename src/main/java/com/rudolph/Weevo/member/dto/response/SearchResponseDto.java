package com.rudolph.Weevo.member.dto.response;

import com.rudolph.Weevo.course.dto.response.CourseListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDto {
    private List<MemberListResponse> memberList;       // 이름 기준 이화인 목록
    private List<MemberListResponse> departmentList;   // 학과 기준 이화인 목록
    private List<CourseListResponse> courseList;       // 강의명 기준 강의 목록
}
