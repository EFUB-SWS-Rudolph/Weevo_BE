package com.rudolph.Weevo.member.dto.request;

import com.rudolph.Weevo.member.domain.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FixProfileRequestDto {
    private String nickname;
    private String dept;
    private String studentId;
    private String location;
    private Boolean isExchange;
    private Boolean isCoffeeChat;
    private Boolean isSkillDonation;
}
