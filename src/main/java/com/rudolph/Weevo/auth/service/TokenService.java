package com.rudolph.Weevo.auth.service;

import com.rudolph.Weevo.auth.dto.TokenResponse;

public interface TokenService {
    TokenResponse reissueAccessToken(String authorizationHeader);
}
