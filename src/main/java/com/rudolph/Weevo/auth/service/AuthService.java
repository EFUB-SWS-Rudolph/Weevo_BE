package com.rudolph.Weevo.auth.service;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import com.rudolph.Weevo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Transactional
    public void logout(CustomUserPrincipal principal, String accessToken) {
        Member member = memberService.findMember(principal.getMemberId());

        String provider = member.getProvider();

        if ("kakao".equals(provider)) {
            logoutFromProvider(provider, accessToken);
        } else {
            logoutFromProvider(provider, accessToken);
        }
    }

    @Transactional
    public void logoutFromProvider(String provider, String accessToken) {
        try {
            if ("kakao".equals(provider)) {
                WebClient.create("https://kapi.kakao.com/v1/user/logout")
                        .post()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();   // 동기처리
            } else {
                WebClient.create("https://oauth2.googleapis.com/revoke?token=" + accessToken)
                        .post()
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            }
        } catch (WebClientResponseException e) {
            log.error("카카오/구글 로그아웃 실패: {}", e.getMessage());
        }
    }

    @Transactional
    public String deleteMember(CustomUserPrincipal principal) {
        try{
            Member member = memberService.findMember(principal.getMemberId());
            String provider = member.getProvider();
            log.info(provider);
            memberRepository.deleteById(member.getId());
            return "회원탈퇴 성공";
        } catch (Exception e) {
            log.error("회원탈퇴 실패: {}", e.getMessage());
            throw new RuntimeException("회원탈퇴 중 오류 발생", e);
        }
    }

//    @Transactional
//    public String deleteMember(CustomUserPrincipal principal, String accessToken) {
//        try {
//            Member member = memberService.findMember(principal.getMemberId());
//            String provider = member.getProvider();
//            log.info(provider);
//            String result;      //회원 탈퇴 결과를 담을 변수 선언
//            if ("kakao".equals(provider)) {
//                result = unlinkKakaoUser(accessToken);
//                log.info(result);
//            } else {
//                result = unlinkGoogleUser(accessToken);
//            }
//            memberRepository.deleteById(member.getId());
//            return "회원탈퇴 성공";
//        } catch (Exception e) {
//            log.error("회원탈퇴 실패: {}", e.getMessage());
//            throw new RuntimeException("회원탈퇴 중 오류 발생", e);
//        }
//    }
//
//    public String unlinkKakaoUser(String accessToken) {
//        String url = "https://kapi.kakao.com/v1/user/unlink";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("Authorization", "Bearer " + accessToken);
//
//        HttpEntity<String> request = new HttpEntity<>("", headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        try {
//            log.info("카카오 unlink 호출 - accessToken: {}", accessToken);
//            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
//            if(response.getStatusCode() == HttpStatus.OK) {
//                return "카카오 unlink 성공: " + response.getBody();
//            } else {
//                return "unlink 실패" + response.getBody();
//            }
//        } catch (Exception e) {
//            log.error("회원탈퇴 중 오류발생 : {}", e.getMessage());
//            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public String unlinkGoogleUser(String accessToken) {
//        String url = "https://oauth2.googleapis.com/revoke";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("token", accessToken);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
//            if (response.getStatusCode() == HttpStatus.OK) {
//                return "구글 unlink 성공";
//            } else {
//                return "구글 unlink 실패: " + response.getBody();
//            }
//        } catch (Exception e) {
//            log.error("구글 회원탈퇴 중 오류 발생: {}", e.getMessage());
//            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
//        }
//    }
}
