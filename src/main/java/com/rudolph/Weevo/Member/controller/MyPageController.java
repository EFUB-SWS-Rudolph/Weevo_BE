/*
package com.rudolph.Weevo.Member.controller;

import com.rudolph.Weevo.Member.dto.response.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class MyPageController {
    private final MemberService memberService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getMyProfile(Authentication auth) {
        Long userId = auth.get();
        UserProfileDto userProfile = memberService.getMyProfile(userId);
        return ResponseEntity.ok(userProfile);
    }
}
*/