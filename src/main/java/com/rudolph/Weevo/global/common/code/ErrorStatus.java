    package com.rudolph.Weevo.global.common.code;

    import com.rudolph.Weevo.global.common.dto.ErrorReasonDto;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import org.springframework.http.HttpStatus;

    @Getter
    @AllArgsConstructor
    public enum ErrorStatus implements BaseErrorCode{
        //전역 에러
        _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "서버 오류"),
        _BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "잘못된 요청입니다."),
        _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401", "인증이 필요합니다."),
        _FORBIDDEN(HttpStatus.FORBIDDEN, "403", "금지된 요청입니다."),

        // 강의
        COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "존재하지 않는 강의입니다."),

        //추가 회원가입 에러
        // === 추가정보 입력 관련 에러 ===
        MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "존재하지 않는 사용자입니다."),
        INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "400", "이화인 인증 코드가 일치하지 않습니다."),
        KEYWORD_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "400", "키워드는 최대 3개까지 선택 가능합니다."),

        //채팅
        CHATROOM_ALREADY_EXITED(HttpStatus.BAD_REQUEST, "400", "이미 나간 채팅방에는 접근할 수 없습니다."),
        CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "채팅방이 존재하지 않습니다."),
        UNAUTHORIZED_CHATROOM_ACCESS(HttpStatus.UNAUTHORIZED, "401", "채팅방에 접근할 권한이 없습니다."),


        // 알림
        ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "알림이 존재하지 않습니다.");

        private final HttpStatus httpStatus;
        private final String code;
        private final String message;

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