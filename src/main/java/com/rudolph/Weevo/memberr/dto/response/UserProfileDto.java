package com.rudolph.Weevo.memberr.dto.response;

import com.rudolph.Weevo.memberr.domain.Member;
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
                .dept(member.getDepartment())
                .isCoffeeChat(member.getCoffeeChat())
                .isSkillDonate(member.getDonation())
                .isExchange(member.getExchange())
                .college(member.getCollege())
                .profileImage(member.getProfileImage() != null ? member.getProfileImage() : "기본프로필사진s3url링크")
                .build();
    }
}
