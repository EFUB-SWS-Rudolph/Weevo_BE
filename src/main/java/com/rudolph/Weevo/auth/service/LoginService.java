package com.rudolph.Weevo.auth.service;

import com.rudolph.Weevo.auth.domain.RefreshToken;
import com.rudolph.Weevo.auth.dto.LoginResponse;
import com.rudolph.Weevo.auth.dto.TokenResponse;
import com.rudolph.Weevo.auth.dto.info.GoogleUserInfo;
import com.rudolph.Weevo.auth.dto.info.KakaoUserInfo;
import com.rudolph.Weevo.auth.dto.info.OAuth2UserInfo;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import com.rudolph.Weevo.auth.repository.RefreshTokenRepository;
import com.rudolph.Weevo.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final WebClient webClient;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret:}")
    private String kakaoClientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoProfileUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;
    private static final String GOOGLE_TOKEN_URI   = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_PROFILE_URI = "https://www.googleapis.com/oauth2/v3/userinfo";

    @Value("${jwt.access_token.expiration_time}")
    private long accessTokenExp;
    @Value("${jwt.refresh_token.expiration_time}")
    private long refreshTokenExp;

    public LoginResponse login(String provider, String code) {

        MultiValueMap<String,String> form = new LinkedMultiValueMap<>();
        form.add("grant_type","authorization_code");
        form.add("client_id", getClientId(provider).trim());
        String secret = getClientSecret(provider).trim();

        if ("google".equals(provider) || !secret.isEmpty()) {
            form.add("client_secret", secret);
        }
        form.add("redirect_uri", getRedirectUri(provider).trim());
        form.add("code", code);

        TokenResponse providerToken;
        try {
            providerToken = webClient.post()
                    .uri(getTokenUri(provider))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .bodyToMono(TokenResponse.class)
                    .block();
            if (providerToken == null) {
                throw new IllegalStateException("Empty token response");
            }
        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            String body  = e.getResponseBodyAsString();
            throw e;
        }

        // 3) 프로필 조회
        String rawAccess = providerToken.getAccessToken();
        Map<String,Object> profile;
        try {
            profile = webClient.get()
                    .uri(getProfileUri(provider))
                    .headers(h -> h.setBearerAuth(rawAccess))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                    .block();
            if (profile == null) {
                throw new IllegalStateException("Empty profile response");
            }
        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            String body  = e.getResponseBodyAsString();
            throw e;
        }

        // 4) 사용자 정보 추출
        OAuth2UserInfo userInfo = switch(provider) {
            case "kakao"  -> new KakaoUserInfo(profile);
            case "google" -> new GoogleUserInfo(profile);
            default       -> throw new IllegalArgumentException("Unsupported provider: "+provider);
        };

        // 5) 회원 조회 또는 신규 가입
        Optional<Member> opt = memberRepository.findByProviderId(userInfo.getProviderId());
        Member member = opt.orElseGet(() -> {
            Member m = Member.builder()
                    .provider(userInfo.getProvider())
                    .providerId(userInfo.getProviderId())
                    .nickName(userInfo.getName())
                    .email(userInfo.getEmail())
                    .build();
            return memberRepository.save(m);
        });
        boolean isNew = opt.isEmpty();

        // 6) 기존 리프레시 토큰 삭제 & 새 JWT 발급
        refreshTokenRepository.deleteByMemberId(member.getId());
        String newRefresh = jwtUtil.generateRefreshToken(member.getId(), refreshTokenExp);
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .memberId(member.getId())
                        .token(newRefresh)
                        .build()
        );
        String newAccess = jwtUtil.generateAccessToken(member.getId(), accessTokenExp);

        // 7) LoginResponse 반환
        return LoginResponse.builder()
                .isNew(isNew)
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .build();
    }

    private String getClientId(String p)     { return "kakao".equals(p) ? kakaoClientId : googleClientId; }
    private String getClientSecret(String p) { return "kakao".equals(p) ? kakaoClientSecret : googleClientSecret; }
    private String getRedirectUri(String p)  { return "kakao".equals(p) ? kakaoRedirectUri : googleRedirectUri; }
    private String getTokenUri(String p)     { return "kakao".equals(p) ? kakaoTokenUri    : GOOGLE_TOKEN_URI; }
    private String getProfileUri(String p)   { return "kakao".equals(p) ? kakaoProfileUri  : GOOGLE_PROFILE_URI; }
}
