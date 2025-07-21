package com.rudolph.Weevo.Auth.controller;

import com.rudolph.Weevo.Auth.dto.TokenResponse;
import com.rudolph.Weevo.Auth.service.TokenService;
import com.rudolph.Weevo.global.common.api.ApiResponse;
import com.rudolph.Weevo.global.common.code.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    //엑세스 토큰을 재발급하는 api
    @GetMapping("/reissue/access-token")
    public ResponseEntity<ApiResponse<Object>> reissueAccessToken(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        TokenResponse accessToken = tokenService.reissueAccessToken(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._CREATED_ACCESS_TOKEN, accessToken);
    }
}
