/*package com.rudolph.Weevo.Member.service;

import com.rudolph.Weevo.Member.domain.Member;
import com.rudolph.Weevo.Member.dto.response.UserProfileDto;
import com.rudolph.Weevo.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)         //프로필 정보 조회
    public UserProfileDto getMyProfile(Long userId) {
        Member member = memberRepository.findById(userId);

    }
}
*/