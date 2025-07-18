package com.rudolph.Weevo.Member.controller;

import com.rudolph.Weevo.Auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.Auth.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
public class Test {

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        CustomUserPrincipal user = SecurityUtil.getCurrentUserPrincipal();

        return ResponseEntity.ok().body(Map.of(
                "memberId", user.getMemberId()
        ));
    }
}

