package com.rudolph.Weevo.member.dto.request;

import lombok.Getter;
import java.util.List;

@Getter
public class UpdateInterestTagRequestDto {
    private List<String> tagNames;
}
