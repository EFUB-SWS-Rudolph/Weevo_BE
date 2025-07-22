package com.rudolph.Weevo.member.dto.response;

import lombok.Data;

@Data
public class UserProfileDto {
    private String userId;
    private String nickname;
    private String phone;
    private String profileImage;
    private String studentId;
    private String dept;
    private boolean coffeeChat;
    private boolean chat;
    private boolean skillDonate;
    private String college;
}
