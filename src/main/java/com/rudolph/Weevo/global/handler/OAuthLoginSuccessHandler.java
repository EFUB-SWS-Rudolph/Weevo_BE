package com.rudolph.Weevo.global.handler;

import com.rudolph.Weevo.global.util.JwtUtil;
import com.rudolph.Weevo.Auth.domain.RefreshToken;
import com.rudolph.Weevo.Auth.dto.info.GoogleUserInfo;
import com.rudolph.Weevo.Auth.dto.info.KakaoUserInfo;
import com.rudolph.Weevo.Auth.dto.info.OAuth2UserInfo;
import com.rudolph.Weevo.Auth.repository.RefreshTokenRepository;
import com.rudolph.Weevo.Member.domain.Member;
import com.rudolph.Weevo.Member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${jwt.redirect}")
    private String REDIRECT_URL;

    @Value("${jwt.access_token.expiration_time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh_token.expiration_time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    private OAuth2UserInfo oAuth2UserInfo = null;

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication; //token
        final String provider = token.getAuthorizedClientRegistrationId(); //provider 추출

        //google, kakao 로그인 요청
        switch (provider){
            case "google" -> {
                log.info("구글 로그인 요청");
                oAuth2UserInfo = new GoogleUserInfo(token.getPrincipal().getAttributes());
            }
            case "kakao" -> {
                log.info("카카오 로그인 요청");
                oAuth2UserInfo = new KakaoUserInfo(token.getPrincipal().getAttributes());
            }
        }

        //정보 추출
        String providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();

        Member existMember = memberRepository.findByProviderId(providerId);
        Member member;

        if(existMember == null){
            // 신규 유저인 경우 -> 추가 정보 화면으로 전환 필요
            log.info("신규 유저입니다.");

            member = Member.builder()
                    .memberId(UUID.randomUUID())
                    .nickName(name)
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .build();
            memberRepository.save(member);

            //토큰 발급
            String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(),REFRESH_TOKEN_EXPIRATION_TIME);
            refreshTokenRepository.save(
                    RefreshToken.builder()
                    .memberId(member.getMemberId())
                            .token(refreshToken)
                            .build()
            );
            String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), ACCESS_TOKEN_EXPIRATION_TIME);

            //추가 정보 입력 페이지로 리다이렉트
            String redirectUri = "http://localhost:3000/signup/additional?accessToken=" + accessToken;
            getRedirectStrategy().sendRedirect(request, response, redirectUri);
            return; // 기존 유저 코드가 실행되지 않게 함
        } else {
            // 기존 유저인경우 -> 리프레쉬 토큰 삭제
            log.info("기존 유저입니다.");
            refreshTokenRepository.deleteByMemberId(existMember.getMemberId());
            member = existMember;
        }

        log.info("유저 이름 : {}", name);
        log.info("PROVIDER : {}", provider);
        log.info("PROVIDER_ID : {}", providerId);

        //리프레쉬 토큰을 발급 후 저장
        String refreshToken =  jwtUtil.generateRefreshToken(member.getMemberId(), REFRESH_TOKEN_EXPIRATION_TIME);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .memberId(member.getMemberId())
                .token(refreshToken)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        //엑세스 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), ACCESS_TOKEN_EXPIRATION_TIME);

        //이름, 엑세스 토큰, 리프레쉬 토큰을 담아 리다이렉트
        String encodedName = URLEncoder.encode(name, "UTF-8");
        String redirectUri = String.format(REDIRECT_URL, encodedName, accessToken, refreshToken);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);

    }
}
