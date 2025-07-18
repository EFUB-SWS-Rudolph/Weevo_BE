package com.rudolph.Weevo.Auth.util;

import com.rudolph.Weevo.Auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.global.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    public static CustomUserPrincipal getCurrentUserPrincipal(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Current Authentication: {}", authentication); // ✅ 인증 정보 확인

        if(authentication == null || !authentication.isAuthenticated()){
            throw new UnauthorizedException("로그인된 사용자가 없습니다");
        }

        Object principal = authentication.getPrincipal();

        log.info("Principal: {}", principal); // ✅ principal이 CustomUserPrincipal인지 확인

        if(principal instanceof CustomUserPrincipal){
            return (CustomUserPrincipal) principal;
        } else {
            throw new UnauthorizedException("인증객체에 CustomUserPrincipal이 없습니다.");
        }
    }
}
