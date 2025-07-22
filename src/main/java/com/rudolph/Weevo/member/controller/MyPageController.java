package com.rudolph.Weevo.member.controller;

import com.rudolph.Weevo.member.dto.response.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "마이페이지 관련 api")
public class MyPageController {
    private final MemberService memberService;

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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserPrincipal principal, @RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace("Bearer ", "");
        memberService.logout(principal, accessToken);

        return ResponseEntity.ok().build();
    }
}
