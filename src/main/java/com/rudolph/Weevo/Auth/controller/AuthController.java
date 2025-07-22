package com.rudolph.Weevo.Auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth2/authorization")
@Tag(name = "Auth", description = "소셜 로그인 관련 API")
public class AuthController {

    @Operation(
            summary = "소셜 로그인 (카카오, 구글)",
            description = """
                    소셜 로그인 시작을 위해 아래 URL로 GET 요청을 보냅니다.
                    
                    **주의:** 실제 로그인은 Spring Security OAuth2가 리다이렉트로 처리하며,
                    이 API는 실제 토큰 발급용이 아니라 문서화용 dummy 엔드포인트입니다.
                    
                    - 호출 URL: /oauth2/authorization/{provider}
                    - provider 값:
                        - kakao
                        - google
                    - 소셜 로그인 성공 시, 서버는 다음 형식으로 프론트로 리디렉트합니다:
                    
                    ```
                    ${JWT_REDIRECT}?accessToken={accessToken}
                    ```
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "302",
                    description = "소셜 로그인 제공자로 리디렉트됨"
            )
    })
    @GetMapping("/{provider}")
    public ResponseEntity<Void> socialLogin(
            @Parameter(description = "소셜 로그인 제공자 (kakao, google)", example = "kakao")
            @PathVariable String provider
    ) {
        // Swagger용 dummy 메서드
        return ResponseEntity.status(302).build();
    }

}

