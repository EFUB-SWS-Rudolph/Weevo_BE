package com.rudolph.Weevo.Member.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "추가정보 요청 dto")
public class InfoRequest {

    @Schema(description = "학과", required = true, example = "컴퓨터공학과")
    private String department;

    @Schema(description = "학번", required = true, example = "2212345")
    private String studentId;

    @Schema(description = "이메일", required = true, example = "user@ewha.ac.kr")
    private String email;

    @Schema(description = "이화인 인증코드", required = true, example = "인증코드")
    private String ewhaAuthCode;

    @Schema(description = "닉네임", required = true, example = "닉네임")
    private String nickname;

    @Schema(description = "주소", required = true, example = "서울시 서대문구")
    private String address;

    @Schema(description = "(선택사항)프로필 사진", example = "ejfijoaej.jpg")
    private String profileImageUrl;

    @Schema(description = "(선택사항)본인의 재능(최대 3가지 선택)", example = "[1,2,3]")
    private List<Long> talent;

    @Schema(description = "본인이 관심있는 분야(최대 3가지 선택)", example = "[4,5,6]")
    private List<Long> interestField;
}
