package com.rudolph.Weevo.global.util;

import com.rudolph.Weevo.global.exception.TokenErrorResult;
import com.rudolph.Weevo.global.exception.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    //엑세스 토큰을 발급하는 메서드
    public String generateAccessToken(Long memberId, long expirationMillis){
        log.info("엑세스 토큰이 발급되었습니다.");

        return Jwts.builder()
                .claim("memberId", memberId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(secretKey)
                .compact();
    }

    //리프레쉬 토큰을 발급하는 메서드
    public String generateRefreshToken(Long memberId, Long expirationMillis){
        String refreshToken = Jwts.builder()
                .claim("memberId", memberId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expirationMillis))
                .signWith(secretKey)
                .compact();

        log.info("리프레쉬 토큰이 발급되었습니다. : {} ", refreshToken);
        return refreshToken;
    }

    //응답 헤더에서 엑세스토큰을 반환하는 메서드
    public String getTokenFromHeader(String authorizationHeader){
        return authorizationHeader.substring(7);
    }

    // Claim 추출
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //토큰에서 유저 id를 반환하는 메서드
    public Long getMemberIdFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get("memberId", Long.class);
//        try {
//            return Jwts.parser()
//                    .verifyWith(secretKey)
//                    .build()
//                    .parseSignedClaims(token)
//                    .getPayload()
//                    .get("memberId", Long.class);
//        } catch (JwtException | IllegalArgumentException e) {
//            throw new TokenException(TokenErrorResult.INVALID_REFRESH_TOKEN);
//        }
    }

    //Jwt 토큰의 유효기간을 확인하는 메서드
    public boolean isTokenExpired(String token){
        try{
            Date expirationDate = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            log.info("토큰의 유효기간을 확인합니다.");
            return expirationDate.before(new Date());
        } catch (JwtException | IllegalArgumentException e){
            log.warn("유효하지 않은 토큰입니다.");
            throw new TokenException(TokenErrorResult.INVALID_REFRESH_TOKEN);
        }
    }
}
