package com.rudolph.Weevo.Auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "소셜 로그인 응답 dto")
public class SocialLoginResponse {

    @Schema(description = "JWT access Token", example = "asdfgh")
    private String accessToken;

    @Schema(description = "JWT Refresh Token", example = "ghaiehgo")
    private String refreshToken;

    @Schema(description = "최초 회원가입 여부", example = "true")
    private boolean isNewMember;

    public SocialLoginResponse(){}

    public SocialLoginResponse(String accessToken, String refreshToken, boolean isNewMember){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isNewMember = isNewMember;
    }
}
