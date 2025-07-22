package com.rudolph.Weevo.chat.dto.summary;

import com.rudolph.Weevo.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpponentSummary {

    private Long userId;
    private String name;
    private String profileImageUrl;

    public static OpponentSummary from(Member member) {
        return OpponentSummary.builder()
                .userId(member.getId())
                .name(member.getNickName())
                .profileImageUrl(member.getProfileImage())
                .build();
    }
}
