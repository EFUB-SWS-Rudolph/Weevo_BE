package com.rudolph.Weevo.temp.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateInterestTagRequestDto {
    private List<Long> tagIds;
}
