package com.rudolph.Weevo.member.controller;

import com.rudolph.Weevo.auth.service.AuthService;
import com.rudolph.Weevo.global.common.api.ApiResponse;
import com.rudolph.Weevo.global.common.code.SuccessStatus;
import com.rudolph.Weevo.member.dto.request.UpdateTalentTagRequestDto;
import com.rudolph.Weevo.member.dto.response.MemberDetailResponse;
import com.rudolph.Weevo.member.dto.response.MemberTalentTagDto;
import com.rudolph.Weevo.member.dto.response.UserProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.member.dto.request.FixProfileRequestDto;
import com.rudolph.Weevo.member.dto.request.UpdateInterestTagRequestDto;
import com.rudolph.Weevo.member.dto.response.MemberInterestTagDto;
import com.rudolph.Weevo.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "마이페이지 관련 api")
public class MyPageController {
    private final MemberService memberService;
    private final AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getMyProfile(@AuthenticationPrincipal CustomUserPrincipal principal) {
        UserProfileDto userProfile = memberService.getMyProfile(principal);
        return ResponseEntity.ok(userProfile);
    }

    @PatchMapping("/profile/basic")
    public ResponseEntity<UserProfileDto> fixMyProfile(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                         @RequestBody FixProfileRequestDto requestDto) {
        UserProfileDto fixedProfile = memberService.fixMyProfile(principal, requestDto);
        return ResponseEntity.ok(fixedProfile);
    }

    @PutMapping("/interest")
    public ResponseEntity<MemberInterestTagDto> fixInterestTag(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                               @RequestBody UpdateInterestTagRequestDto requestDto) {
        MemberInterestTagDto fixedInterestTag = memberService.updateInterestTag(principal, requestDto);
        return ResponseEntity.ok(fixedInterestTag);
    }

    @PutMapping("/talent")
    public ResponseEntity<MemberTalentTagDto> fixTalentTag(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                           @RequestBody UpdateTalentTagRequestDto requestDto) {
        MemberTalentTagDto fixedTalentTag = memberService.updateTalentTag(principal, requestDto);
        return ResponseEntity.ok(fixedTalentTag);
    }

    @Operation(summary = "프로필 이미지 수정", description = "사용자의 프로필 이미지를 변경합니다.")
    @PatchMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> updateProfileImage(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                          @RequestPart(value = "image")MultipartFile imageFile) {
        String imageUrl = memberService.updateProfileImage(principal, imageFile);
        return ApiResponse.onSuccess(SuccessStatus._OK, imageUrl);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserPrincipal principal, @RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace("Bearer ", "");
        authService.logout(principal, accessToken);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal CustomUserPrincipal principal, @RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace("Bearer ", "");
        authService.deleteMember(principal, accessToken);

        SecurityContextHolder.clearContext(); // 현재 세션에서 인증 정보 제거
        return ResponseEntity.noContent().build();
}
