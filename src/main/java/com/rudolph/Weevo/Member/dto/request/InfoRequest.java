package com.rudolph.Weevo.Member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @NotBlank
    private String email;
    @NotBlank
    private String ewhaAuthCode; // 이화인 인증 코드

    @Size(min = 0, max =3)
    private List<String> interestKeywords;
    @Size(min = 0, max =3)
    private List<String> talentKeywords;




}
