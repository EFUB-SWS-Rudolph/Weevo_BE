package com.rudolph.Weevo.global.exception;

import com.rudolph.Weevo.global.common.code.BaseErrorCode;
import com.rudolph.Weevo.global.common.dto.ErrorReasonDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorResult implements BaseErrorCode {
    INVALID_TOKEN("유효하지 않은 토큰입니다.", "401", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN("유효하지 않은 엑세스 토큰입니다.", "401", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레쉬 토큰입니다.", "401", HttpStatus.UNAUTHORIZED);

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

   @Override
    public ErrorReasonDto getReason(){
       return ErrorReasonDto.builder()
               .isSuccess(false)
               .code(code)
               .message(message)
               .build();
   }

   @Override
    public ErrorReasonDto getReasonHttpStatus(){
       return ErrorReasonDto.builder()
               .isSuccess(false)
               .httpStatus(httpStatus)
               .code(code)
               .message(message)
               .build();
   }
}
