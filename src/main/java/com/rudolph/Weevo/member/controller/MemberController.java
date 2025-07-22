package com.rudolph.Weevo.member.controller;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원 관련 api")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    //추가 회원 정보 기입
    @PostMapping("/info")
    public ResponseEntity<String> submitAdditionalInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid InfoRequest request
    ){
        memberService.submitAdditionalInfo(principal, request);
        return ResponseEntity.ok("추가 정보 입력 완료");
    }

    //이화인 목록 조회
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MemberListResponse>> getMembers(
            @RequestParam(required = false) String nickName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) Boolean coffeeChat,
            @RequestParam(required = false) Boolean donation,
            @RequestParam(required = false) Boolean exchange,
            @RequestParam(defaultValue = "desc") String sort,
            @AuthenticationPrincipal CustomUserPrincipal user
    ){
        Long memberId = user.getMemberId();
        List<MemberListResponse> result = memberService.findMembers(
                nickName, department, college, coffeeChat, donation, exchange, sort
        );
        return ResponseEntity.ok(result);
    }

    //이화인 상세 조회
    @GetMapping("/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberDetailResponse> getMemberDetail(
            @PathVariable("memberId") Long id,
            @AuthenticationPrincipal CustomUserPrincipal user
    ){
        Member member = memberRepository.findById(id)
                .orElseThrow(()-> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        Long memberId = member.getId();
        MemberDetailResponse result = memberService.findMemberDetail(memberId);
        return ResponseEntity.ok(result);
    }

}
