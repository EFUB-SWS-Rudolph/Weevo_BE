package com.rudolph.Weevo.Member.controller;

import com.rudolph.Weevo.Auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.Member.dto.request.InfoRequest;
import com.rudolph.Weevo.Member.dto.response.InfoResponse;
import com.rudolph.Weevo.Member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원 관련 api")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/info")
    public ResponseEntity<String> submitAdditionalInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid InfoRequest request
    ){
        memberService.submitAdditionalInfo(principal, request);
        return ResponseEntity.ok("추가 정보 입력 완료");
    }




}
