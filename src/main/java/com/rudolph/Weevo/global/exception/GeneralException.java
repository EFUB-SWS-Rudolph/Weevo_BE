package com.rudolph.Weevo.global.exception;

import com.rudolph.Weevo.global.common.code.ErrorStatus;

public class GeneralException extends RuntimeException {

    private final ErrorStatus status;

    public GeneralException(ErrorStatus status) {

        super(status.getMessage());
        this.status = status;
    }

    public ErrorStatus getStatus(){
        return status;
    }
}
