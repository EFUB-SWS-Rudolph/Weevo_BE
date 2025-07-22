package com.rudolph.Weevo.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class InfoRequest {
    @NotBlank
    private String nickName;
    @NotBlank
    private String location;
    @NotBlank
    private String studentId;
    @NotBlank
    private String college; //대학
    @NotBlank
    private String department; //학과

    @Size(min = 0, max =3)
    private List<String> interestKeywords;
    @Size(min = 0, max =3)
    private List<String> talentKeywords;




}
