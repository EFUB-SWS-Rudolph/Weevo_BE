package com.rudolph.Weevo.member.controller;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.global.common.api.ApiResponse;
import com.rudolph.Weevo.global.common.code.SuccessStatus;
import com.rudolph.Weevo.member.dto.response.MemberDetailResponse;
import com.rudolph.Weevo.member.dto.response.MemberListResponse;
import com.rudolph.Weevo.member.repository.MemberRepository;
import com.rudolph.Weevo.member.service.MemberService;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.dto.request.InfoRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원 관련 api")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    //추가 회원 정보 기입
    @PostMapping("/info")
    public ResponseEntity<ApiResponse<String>> submitAdditionalInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid InfoRequest request
    ){
        memberService.submitAdditionalInfo(principal, request);
        return ApiResponse.onSuccess(SuccessStatus._ADDITIONAL_INFO_SUBMITTED,null);
    }

    //이화인 목록 조회
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<MemberListResponse>>> getMembers(
            @RequestParam(required = false) String nickName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) Boolean coffeeChat,
            @RequestParam(required = false) Boolean donation,
            @RequestParam(required = false) Boolean exchange,
            @RequestParam(defaultValue = "desc") String sort,
            @AuthenticationPrincipal CustomUserPrincipal user
    ){
        UUID memberId = user.getMemberId();
        List<MemberListResponse> result = memberService.findMembers(
                nickName, department, college, coffeeChat, donation, exchange, sort
        );
        return ApiResponse.onSuccess(SuccessStatus._MEMBER_LIST_RETRIEVED, result);
    }

    //이화인 상세 조회
    @GetMapping("/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> getMemberDetail(
            @PathVariable("memberId") Long id,
            @AuthenticationPrincipal CustomUserPrincipal user
    ){
        Member member = memberRepository.findById(id)
                .orElseThrow(()-> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        UUID memberId = member.getMemberId();
        MemberDetailResponse result = memberService.findMemberDetail(memberId);
        return ApiResponse.onSuccess(SuccessStatus._DETAIL_RETRIVED, result);
    }

}
