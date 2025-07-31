package com.rudolph.Weevo.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberTagsResponseDto {
    private MemberInterestTagDto interest;
    private MemberTalentTagDto talent;
}
