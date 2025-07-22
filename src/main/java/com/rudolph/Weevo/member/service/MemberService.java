package com.rudolph.Weevo.member.service;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.domain.MemberTalentTag;
import com.rudolph.Weevo.member.dto.request.InfoRequest;
import com.rudolph.Weevo.member.dto.request.UpdateTalentTagRequestDto;
import com.rudolph.Weevo.member.dto.response.*;
import com.rudolph.Weevo.member.repository.MemberRepository;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.member.repository.MemberTalentTagRepository;
import com.rudolph.Weevo.tag.domain.Tag;
import com.rudolph.Weevo.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rudolph.Weevo.member.domain.MemberInterestTag;
import com.rudolph.Weevo.member.dto.request.FixProfileRequestDto;
import com.rudolph.Weevo.member.dto.request.UpdateInterestTagRequestDto;
import com.rudolph.Weevo.member.repository.MemberInterestTagRepository;
import com.rudolph.Weevo.member.repository.MemberTagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TagService tagService;
    private final MemberInterestTagRepository memberInterestTagRepository;
    private final MemberTagRepository memberTagRepository;
    private final MemberTalentTagRepository memberTalentTagRepository;

    // 1) 추가 회원 정보 가입
    @Transactional
    public void submitAdditionalInfo(CustomUserPrincipal principal, InfoRequest request){

        Long memberId = principal.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));


        if(request.getInterestKeywords().size() > 3 || request.getTalentKeywords().size() > 3){
            throw new GeneralException(ErrorStatus.KEYWORD_LIMIT_EXCEEDED);
        }

        List<Tag> interestTags = tagService.findByNames(request.getInterestKeywords());
        List<Tag> talentTags = tagService.findByNames(request.getTalentKeywords());

        member.additionalInfo(
                request.getNickName(),
                request.getStudentId(),
                request.getCollege(),
                request.getDepartment(),
                request.getLocation(),
                interestTags,
                talentTags
        );
    }

    // 2) 이화인 목록 조회
    @Transactional(readOnly = true)
    public List<MemberListResponse> findMembers(String nickName, String department, String college,
                                                Boolean coffeeChat, Boolean donation, Boolean exchange,
                                                String sortDirection){

        Sort sort;
        if ("desc".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt"); // 예: createdAt 기준 내림차순 정렬
        } else {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");  // 예: createdAt 기준 오름차순 정렬
        }
        return memberRepository.findByMembersWithFilters(nickName, department, college, coffeeChat, donation,
                exchange, sort);
    }

    // 3) 이화인 상세 조회
    @Transactional(readOnly = true)
    public MemberDetailResponse findMemberDetail(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        return MemberDetailResponse.from(member);
    }

    // 4) Member 객체
    @Transactional(readOnly = true)
    public Member findMember(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(()-> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }
    

    @Transactional(readOnly = true) //프로필 정보 조회
    public UserProfileDto getMyProfile(CustomUserPrincipal principal) {
        // 로그인된 사용자 ID 사용
        Long memberId = principal.getMemberId();
        Member member = findMember(memberId);
        return UserProfileDto.from(member);
    }

    @Transactional  //프로필 수정 (관심 태그, 사진 제외)
    public UserProfileDto fixMyProfile(CustomUserPrincipal principal, FixProfileRequestDto requestDto) {
        Long memberId = principal.getMemberId();
        Member member = findMember(memberId);
        member.updateProfile(requestDto);
        return UserProfileDto.from(member);
    }

    @Transactional //관심 태그 수정
    public MemberInterestTagDto updateInterestTag(CustomUserPrincipal principal, UpdateInterestTagRequestDto requestDto) {
        Long memberId = principal.getMemberId();
        Member member = findMember(memberId);

        //기존 관심 태그 제거
        memberInterestTagRepository.deleteByMember(member);

        //새로 설정된 태그 추가
        List<MemberInterestTag> newInterestTags = new ArrayList<>();
        for (Long tagId: requestDto.getTagIds()) {
            Tag tag = memberTagRepository.findById(tagId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.TAG_NOT_FOUND));
            newInterestTags.add(
                    MemberInterestTag.builder()
                    .member(member)
                    .tag(tag)
                    .build());
        }
        memberInterestTagRepository.saveAll(newInterestTags);
        return MemberInterestTagDto.from(member, newInterestTags);
    }

    @Transactional //재능 태그 수정
    public MemberTalentTagDto updateTalentTag(CustomUserPrincipal principal, UpdateTalentTagRequestDto requestDto) {
        Long memberId = principal.getMemberId();
        Member member = findMember(memberId);

        //기존 재능 태그 제거
        memberTalentTagRepository.deleteByMember(member);

        //새로 설정된 태그 추가
        List<MemberTalentTag> newTalentTags = new ArrayList<>();
        for (Long tagId: requestDto.getTagIds()) {
            Tag tag = memberTagRepository.findById(tagId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.TAG_NOT_FOUND));
            newTalentTags.add(
                    MemberTalentTag.builder()
                            .member(member)
                            .tag(tag)
                            .build());
        }
        memberTalentTagRepository.saveAll(newTalentTags);
        return MemberTalentTagDto.from(member, newTalentTags);
    }

    @Transactional                  //accessToken 프론트에서 받아와야하나?
    public void logout(CustomUserPrincipal principal, String accessToken) {
        Member member = findMember(principal.getMemberId());

        String provider = member.getProvider();

        if ("kakao".equals(provider)) {
            logoutFromProvider(provider, accessToken);
        } else {
            logoutFromProvider(provider, accessToken);
        }
    }

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
            log.error("카카오 로그아웃 실패: {}", e.getMessage());
        }
    }

}
