package com.rudolph.Weevo.global.common.code;

import com.rudolph.Weevo.global.common.dto.ErrorReasonDto;

public interface BaseErrorCode {
    public ErrorReasonDto getReason();

    public ErrorReasonDto getReasonHttpStatus();
}
