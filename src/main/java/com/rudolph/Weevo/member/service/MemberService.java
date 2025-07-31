package com.rudolph.Weevo.member.service;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.global.service.S3Service;
import com.rudolph.Weevo.member.domain.Department;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.domain.MemberTalentTag;
import com.rudolph.Weevo.member.dto.request.InfoRequest;
import com.rudolph.Weevo.member.dto.request.UpdateTalentTagRequestDto;
import com.rudolph.Weevo.member.dto.response.*;
import com.rudolph.Weevo.member.repository.*;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.tag.domain.Tag;
import com.rudolph.Weevo.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rudolph.Weevo.member.domain.MemberInterestTag;
import com.rudolph.Weevo.member.dto.request.FixProfileRequestDto;
import com.rudolph.Weevo.member.dto.request.UpdateInterestTagRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
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
    private final S3Service s3Service;
    private final DepartmentRepository departmentRepository;

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

        Department department = findDepartment(request.getDepartment());
        member.additionalInfo(
                request.getNickName(),
                request.getStudentId(),
                request.getCollege(),
                department,
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

    @Transactional(readOnly = true) //프로필 태그 조회
    public MemberTagsResponseDto getMyTagProfile(CustomUserPrincipal principal) {
        Long memberId = principal.getMemberId();
        //태그 가져오기
        MemberInterestTagDto interestTags = getInterestTags(memberId);
        MemberTalentTagDto talentTags = getTalentTags(memberId);

        MemberTagsResponseDto responseDto = new MemberTagsResponseDto(interestTags, talentTags);
        return responseDto;
    }

    private MemberInterestTagDto getInterestTags(Long memberId) {
        Member member = findMember(memberId);
        List<MemberInterestTag> interestTags = memberInterestTagRepository.findAllByMember(member);
        return MemberInterestTagDto.from(member, interestTags);
    }

    private MemberTalentTagDto getTalentTags(Long memberId) {
        Member member = findMember(memberId);
        List<MemberTalentTag> talentTags = memberTalentTagRepository.findAllByMember(member);
        return MemberTalentTagDto.from(member, talentTags);
    }

    @Transactional  //프로필 수정 (관심 태그, 사진 제외)
    public UserProfileDto fixMyProfile(CustomUserPrincipal principal, FixProfileRequestDto requestDto) {
        Long memberId = principal.getMemberId();
        Member member = findMember(memberId);
        Department department;
        if (requestDto.getDept() == null) {
            department = member.getDepartment();
        }else {
            department = findDepartment(requestDto.getDept());
        }
        member.updateProfile(requestDto, department);
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

    @Transactional
    public String updateProfileImage(CustomUserPrincipal principal, MultipartFile imageFile) {
        Long memberId = principal.getMemberId();
        Member member = findMember(memberId);

        String imageUrl = s3Service.uploadFile(imageFile, "/profile" + memberId);
        member.updateProfileImage(imageUrl);
        return imageUrl;
    }

    @Transactional(readOnly = true)
    public Department findDepartment(String deptName) {
        Department dept = departmentRepository.findByName(deptName).orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_DEPARTMENT));
        return dept;
    }
}
