package com.rudolph.Weevo.global.common.code;

import com.rudolph.Weevo.global.common.dto.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
//성공시 보낼 응답 코드
public enum SuccessStatus implements BaseCode {
    //전역 응답 코드
    _OK(HttpStatus.OK, "200", "성공입니다."),
    _CREATED(HttpStatus.CREATED, "201", "생성에 성공했습니다."),

    //커스텀
    _CREATED_ACCESS_TOKEN(HttpStatus.CREATED, "201", "엑세스 토큰 재발급에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason(){
        return ReasonDto.builder()
                .isSuccess(true)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus(){
        return ReasonDto.builder()
                .isSuccess(true)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }

}
