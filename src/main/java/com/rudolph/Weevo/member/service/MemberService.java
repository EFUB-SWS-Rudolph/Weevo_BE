package com.rudolph.Weevo.member.service;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.dto.request.InfoRequest;
import com.rudolph.Weevo.member.dto.response.MemberDetailResponse;
import com.rudolph.Weevo.member.dto.response.MemberListResponse;
import com.rudolph.Weevo.member.repository.MemberRepository;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.tag.domain.Tag;
import com.rudolph.Weevo.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TagService tagService;

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

}
