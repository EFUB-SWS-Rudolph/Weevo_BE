package com.rudolph.Weevo.Auth.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "소셜 로그인 요청 dto")
public class SocialLoginRequest {

    @Schema(description = "소셜 엑세스 토큰", example = "abc.def,ghi")
    private String socialAccessToken;
}
