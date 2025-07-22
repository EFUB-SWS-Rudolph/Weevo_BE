package com.rudolph.Weevo.memberr.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "추가정보 입력 응답 dto")
public class InfoResponse {

    @Schema(description = "결과 메시지", example = "회원가입 성공")
    private String message;

    public InfoResponse(String message){
        this.message = message;
    }
}
