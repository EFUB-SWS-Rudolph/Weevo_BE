package com.rudolph.Weevo.auth.dto.info;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes;

    @Override
    public String getProvider(){
        return "kakao";
    }

    @Override
    public String getProviderId(){
        //id가 long 타입이므로 string으로 변환
        return attributes.get("id").toString();
    }

    @Override
    public String getName(){
        //kakao_account라는 맵에서 추출
        return (String) ((Map<?, ?>) attributes.get("properties")).get("nickname");
    }

    @Override
    public String getEmail(){
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

}
