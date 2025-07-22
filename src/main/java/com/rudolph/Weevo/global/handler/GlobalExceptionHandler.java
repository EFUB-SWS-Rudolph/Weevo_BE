package com.rudolph.Weevo.global.handler;

import com.rudolph.Weevo.global.common.api.ApiResponse;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.global.exception.TokenErrorResult;
import com.rudolph.Weevo.global.exception.TokenException;
import com.rudolph.Weevo.global.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 0) 도메인 예외 처리
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(GeneralException e) {
        return ApiResponse.onFailure(e.getStatus());
    }

    // 1) TokenException
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenException(TokenException e) {
        TokenErrorResult errorResult = e.getTokenErrorResult();
        return ApiResponse.onFailure(errorResult);
    }

    // 2) UnauthorizedException
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException e) {
        return ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED);
    }

    // 3) 회원을 못 찾았을 때 (findByMemberId → NoSuchElementException)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(NoSuchElementException e) {
        // ErrorStatus.MEMBER_NOT_FOUND 는 404, “존재하지 않는 사용자입니다.”
        return ApiResponse.onFailure(ErrorStatus.MEMBER_NOT_FOUND);
    }

    // 4) 잘못된 파라미터, 검증 실패 등
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(IllegalArgumentException e) {
        return ApiResponse.onFailure(ErrorStatus._BAD_REQUEST);
    }

    // 5) S3 업로드/다운로드 실패
    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleS3Error(S3Exception e) {
        log.error("S3 error: {}", e.awsErrorDetails().errorMessage(), e);
        return ResponseEntity
                .status(ErrorStatus._INTERNAL_SERVER_ERROR.getReasonHttpStatus().getHttpStatus())
                .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR).getBody());
    }

    // 6) 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAll(Exception e) {
        log.error("Unhandled exception:", e);
        return ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR);
    }
}
