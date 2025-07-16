package com.rudolph.Weevo.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TokenErrorResult {
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    MALFORMED_TOKEN("토큰 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    TokenErrorResult(String message, HttpStatus httpStatus){
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
