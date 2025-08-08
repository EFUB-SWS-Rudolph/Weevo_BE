package com.rudolph.Weevo.search.dto.response;

import com.rudolph.Weevo.course.dto.response.CourseListResponse;
import com.rudolph.Weevo.member.dto.response.MemberListResponse;
import lombok.*;

import java.util.List;

@Getter @Builder @AllArgsConstructor
public class SearchResponse {
    private List<CourseListResponse> courses;
    private List<MemberListResponse> membersByNickname;
    private List<MemberListResponse> membersByDept;
}
