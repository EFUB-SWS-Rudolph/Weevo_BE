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
                .memberId(member.getId() != null ? member.getId().toString() : "")
                .nickname(member.getNickName() != null ? member.getNickName() : "")
                .studentId(member.getStudentId() != null ? member.getStudentId() : "")
                .location(member.getLocation() != null ? member.getLocation() : "")
                .dept(member.getDepartment() != null ? member.getDepartment() : "")
                .isCoffeeChat(Boolean.TRUE.equals(member.getCoffeeChat()))
                .isExchange(Boolean.TRUE.equals(member.getExchange()))
                .isSkillDonate(Boolean.TRUE.equals(member.getDonation()))
                .college(member.getCollege() != null ? member.getCollege() : "")
                .profileImage(member.getProfileImage() != null
                        ? member.getProfileImage()
                        : "/images/general_profile.jpg")
                .build();
    }
}
