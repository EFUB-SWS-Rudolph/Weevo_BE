package com.rudolph.Weevo.auth.service;

import com.rudolph.Weevo.auth.domain.RefreshToken;
import com.rudolph.Weevo.auth.dto.TokenResponse;
import com.rudolph.Weevo.auth.repository.RefreshTokenRepository;
import com.rudolph.Weevo.global.exception.TokenErrorResult;
import com.rudolph.Weevo.global.exception.TokenException;
import com.rudolph.Weevo.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;  //엑세스 토큰 유효시간

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Override
    public TokenResponse reissueAccessToken(String authorizationHeader){
        String refreshToken = jwtUtil.getTokenFromHeader(authorizationHeader);
        Long memberId = jwtUtil.getMemberIdFromToken(refreshToken);
        RefreshToken existRefreshToken = refreshTokenRepository.findByMemberId(memberId);
        String accessToken = null;

        if(!existRefreshToken.getToken().equals(refreshToken) || jwtUtil.isTokenExpired(refreshToken)){
            //리프레쉬 토큰이 다르거나 만료된 경우
            throw new TokenException(TokenErrorResult.INVALID_REFRESH_TOKEN);
        } else {
            //엑세스 토큰 재발급
            accessToken = jwtUtil.generateAccessToken(memberId, ACCESS_TOKEN_EXPIRATION_TIME);
        }

        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
