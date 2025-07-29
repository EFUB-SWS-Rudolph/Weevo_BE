package com.rudolph.Weevo.auth.controller;

import com.rudolph.Weevo.auth.dto.TokenRequest;
import com.rudolph.Weevo.auth.dto.TokenResponse;
//import com.rudolph.Weevo.auth.service.TokenService;
import com.rudolph.Weevo.auth.service.TokenServiceImpl;
import com.rudolph.Weevo.global.common.api.ApiResponse;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.common.code.SuccessStatus;
import com.rudolph.Weevo.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TokenController {

    private final TokenServiceImpl tokenService;
    private final JwtUtil jwtUtil;

    //엑세스 토큰을 재발급하는 api
    @PostMapping("/reissue/access-token")
    public ResponseEntity<ApiResponse<Object>> reissueAccessToken(
            @RequestBody TokenRequest tokenRequest
            ) {
        String refreshToken = tokenRequest.getRefreshToken();
        if(refreshToken == null || refreshToken.isEmpty()){
            return ApiResponse.onFailure(ErrorStatus._REFRESH_TOKEN_NOT_FOUND);
        }
        TokenResponse accessToken = tokenService.reissueAccessToken(refreshToken);
        return ApiResponse.onSuccess(SuccessStatus._CREATED_ACCESS_TOKEN, accessToken);
    }
}
