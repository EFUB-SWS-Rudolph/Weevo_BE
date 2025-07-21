package com.rudolph.Weevo.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;  //재발행 성공시에 보낼 토큰 dto
}
