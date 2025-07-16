package com.rudolph.Weevo.global.exception;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException{
    private final TokenErrorResult  errorResult;

    public TokenException(TokenErrorResult errorResult){
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
}
