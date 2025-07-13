package com.rudolph.Weevo.Auth.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "소셜 로그인 응답 dto")
public class SocialLoginResponse {

    @Schema(description = "사용자 이메일", example = "user@ewha.ac.kr")
    private String email;

    @Schema(description = "최초 회원가입 여부 (true = 추가정보 입력 필요)", example = "true")
    private boolean isNewUser;

    public SocialLoginResponse(String email, boolean isNewUser){
        this.email= email;
        this.isNewUser = isNewUser;
    }
}
