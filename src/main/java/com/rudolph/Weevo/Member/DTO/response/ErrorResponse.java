package com.rudolph.Weevo.Member.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "에러 응답 DTO")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "400")
    private int status;

    @Schema(description = "에러 메시지", example = "유효하지 않은 인가 코드입니다.")
    private String message;

    public ErrorResponse() {}

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}