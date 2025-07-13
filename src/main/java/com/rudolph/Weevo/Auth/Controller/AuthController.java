package com.rudolph.Weevo.Auth.Controller;

import com.rudolph.Weevo.Auth.DTO.request.SocialLoginRequest;
import com.rudolph.Weevo.Auth.DTO.response.SocialLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@Tag(name = "Auth", description = "소셜 로그인 관련 api")
public class AuthController {

    //소셜 로그인 처리
    @Operation(
            summary = "소셜 로그인 (카카오, 구글)",
            description = "프론트에서 발급받은 소셜 엑세스 토큰을 전달받아 로그인 처리합니다."
                    + "최초 회원가입인지 여부도 함께 반환합니다."
                    + "provider에는 kakao와 google이 있습니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "소셜 로그인 성공 여부 및 최초 회원가입 여부 반환",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SocialLoginResponse.class)
            )
    )
    @PostMapping("/{provider}")
    public SocialLoginResponse socialLogin(
            @PathVariable String provider,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "소셜 로그인 요청 dto",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SocialLoginRequest.class)
                    )
            )
            @RequestBody SocialLoginRequest request
    ){
        //실제 로직
        boolean isNewUser = true;
        String email = "user@ewha.ac.kr";

        return new SocialLoginResponse(email, isNewUser);
    }


}
