package com.rudolph.Weevo.Member.service;

import com.rudolph.Weevo.Auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.Member.domain.Member;
import com.rudolph.Weevo.Member.dto.request.InfoRequest;
import com.rudolph.Weevo.Member.repository.MemberRepository;
import com.rudolph.Weevo.tag.domain.Tag;
import com.rudolph.Weevo.tag.service.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import com.rudolph.Weevo.Member.dto.response.UserProfileDto;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TagService tagService;

    private static final String AUTH_CODE = "응뎡뎍"; // 인증 코드

    @Transactional
    public void submitAdditionalInfo(CustomUserPrincipal principal, InfoRequest request){

        UUID memberId = principal.getMemberId();
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 사용자입니다."));

        if(!AUTH_CODE.equals((request.getEwhaAuthCode()))){
            throw new IllegalArgumentException("이화인 인증 코드가 일치하지 않습니다.");
        }

        if(request.getInterestKeywords().size() > 3 || request.getTalentKeywords().size() > 3){
            throw new IllegalArgumentException("키워드는 최대 3개까지 선택 가능합니다.");
        }

        List<Tag> interestTags = tagService.findByNames(request.getInterestKeywords());
        List<Tag> talentTags = tagService.findByNames(request.getTalentKeywords());

        member.additionalInfo(
                request.getNickName(),
                request.getStudentId(),
                request.getCollege(),
                request.getDepartment(),
                request.getEmail(),
                request.getLocation(),
                interestTags,
                talentTags
        );
    }
}

    @Transactional(readOnly = true)         //프로필 정보 조회
    public UserProfileDto getMyProfile(Long userId) {
        Member member = memberRepository.findById(userId);

    }
}
