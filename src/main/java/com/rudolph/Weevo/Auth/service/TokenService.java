package com.rudolph.Weevo.Auth.service;

import com.rudolph.Weevo.Auth.dto.TokenResponse;

public interface TokenService {
    TokenResponse reissueAccessToken(String authorizationHeader);
}
