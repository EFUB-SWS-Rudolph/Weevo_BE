package com.rudolph.Weevo.auth.controller;

import com.rudolph.Weevo.auth.dto.LoginRequest;
import com.rudolph.Weevo.auth.dto.LoginResponse;
import com.rudolph.Weevo.auth.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final LoginService loginService;

    @GetMapping("/login/oauth2/code/{provider}")
    public ResponseEntity<Void> oauth2Callback() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable String provider,
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(loginService.login(provider, request.getCode()));
    }
}


