package com.rudolph.Weevo.global.common.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorReasonDto {
    private HttpStatus httpStatus;
    private final boolean isSuccess;
    private String code;
    private String message;
}
