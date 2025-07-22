package com.rudolph.Weevo.Member.service;

import com.rudolph.Weevo.Auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.Member.domain.Member;
import com.rudolph.Weevo.Member.dto.request.InfoRequest;
import com.rudolph.Weevo.Member.repository.MemberRepository;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.tag.domain.Tag;
import com.rudolph.Weevo.tag.service.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
}
