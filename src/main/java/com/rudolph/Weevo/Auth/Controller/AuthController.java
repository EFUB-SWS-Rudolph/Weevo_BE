package com.rudolph.Weevo.Auth.Controller;

import com.rudolph.Weevo.Auth.DTO.request.SocialLoginRequest;
import com.rudolph.Weevo.Auth.DTO.response.ErrorResponse;
import com.rudolph.Weevo.Auth.DTO.response.SocialLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@Tag(name = "Auth", description = "소셜 로그인 관련 api")
public class AuthController {

    //소셜 로그인 처리
    @Operation(
            summary = "소셜 로그인 (카카오, 구글)",
            description = "소셜 로그인 제공자로부터 인가 코드를 받아 우리 서비스 JWT 토큰을 발급합니다."
                    + "최초 회원가입인지 여부도 함께 반환합니다."
                    + "provider에는 kakao와 google이 있습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "소로그인 성공 및 토큰 발급",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SocialLoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 인가 코드",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{provider}")
    public ResponseEntity<SocialLoginResponse> socialLogin(
            @Parameter(description = "소셜 로그인 제공자 (kakao. google)", example = "kakao")
            @PathVariable String provider,

            @Parameter(description = "소셜 로그인 인가 코드", example = "abc123")
            @RequestParam String code
    ){
        //swagger 문서용 controller
        return ResponseEntity.ok(new SocialLoginResponse(
                "jwt-access-token",
                "jwt refresh-token",
                true
        ));
    }


}
