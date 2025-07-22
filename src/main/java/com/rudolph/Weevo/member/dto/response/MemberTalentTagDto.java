package com.rudolph.Weevo.member.dto.response;

import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.domain.MemberTalentTag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MemberTalentTagDto {
    private Long memberId;
    private List<String> tagNames;

    public static MemberTalentTagDto from(Member member, List<MemberTalentTag> talentTags) {
        List<String> tagNames = talentTags.stream()
                .map(talentTag -> talentTag.getTag().getName())
                .toList();

        return new MemberTalentTagDto(member.getId(), tagNames);
    }
}
