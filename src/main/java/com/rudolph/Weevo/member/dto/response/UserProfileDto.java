package com.rudolph.Weevo.member.dto.response;

import com.rudolph.Weevo.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserProfileDto {
    private String memberId;
    private String nickname;
    private String studentId;
    private String location;
    private String dept;
    private boolean isCoffeeChat;
    private boolean isExchange;
    private boolean isSkillDonate;
    private String college;
    private String profileImage;

    public static UserProfileDto from(Member member) {
        return UserProfileDto.builder()
                .memberId(member.getId().toString())
                .nickname(member.getNickName())
                .studentId(member.getStudentId())
                .location(member.getLocation())
                .dept(member.getDepartment().getName())
                .isCoffeeChat(member.getCoffeeChat())
                .isSkillDonate(member.getDonation())
                .isExchange(member.getExchange())
                .college(member.getCollege())
                .profileImage(member.getProfileImage() != null ? member.getProfileImage() : "/images/general_profile.jpg")
                .build();
    }
}
