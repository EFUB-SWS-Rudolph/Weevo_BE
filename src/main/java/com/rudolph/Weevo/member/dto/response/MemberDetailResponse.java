package com.rudolph.Weevo.member.dto.response;

import com.rudolph.Weevo.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MemberDetailResponse {
    private static final String DEFAULT_PROFILE_IMAGE_URL = "/images/스크린샷 2025-07-20 오전 1.33.39.png";

    private String nickName;
    private String department;
    private String profileImage;
    private List<String> talentTags;
    private boolean coffeeChat;
    private boolean donation;
    private boolean exchange;

    public static MemberDetailResponse from (Member member){

        String profileImage = member.getProfileImage();
        if(profileImage == null || profileImage.isBlank()){
            profileImage = DEFAULT_PROFILE_IMAGE_URL;
        }  //테스트를 위해 넣은 코드라 나중에 빼주세요

        return MemberDetailResponse.builder()
                .nickName(member.getNickName())
                .department(member.getDepartment())
                .profileImage(member.getProfileImage())
                .talentTags(member.getTalentTags().stream()
                        .map(talentTag -> talentTag.getTag().getName())
                        .collect(Collectors.toList()))
                .coffeeChat(Boolean.TRUE.equals(member.getCoffeeChat()))
                .donation(Boolean.TRUE.equals(member.getDonation()))
                .exchange(Boolean.TRUE.equals(member.getExchange()))
                .build();
    }
}
