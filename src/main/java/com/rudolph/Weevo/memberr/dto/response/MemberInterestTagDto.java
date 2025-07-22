package com.rudolph.Weevo.memberr.dto.response;

import com.rudolph.Weevo.memberr.domain.Member;
import com.rudolph.Weevo.memberr.domain.MemberInterestTag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MemberInterestTagDto {
    private Long memberId;
    private List<String> tagNames;

    public static MemberInterestTagDto from(Member member, List<MemberInterestTag> interestTags) {
        List<String> tagNames = interestTags.stream()
                .map(interestTag -> interestTag.getTag().getName())
                .toList();

        return new MemberInterestTagDto(member.getId(), tagNames);
    }
}
